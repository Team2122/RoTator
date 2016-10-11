#!/usr/bin/env bash

set -e

log() {
	echo -e "\e[32m$@\e[0m"
}

err() {
	echo -e "\e[31m$@\e[0m"
}

: ${TEAM_NUMBER:=212}
: ${RIO_HOST:=roboRIO-${TEAM_NUMBER}-FRC.local}
: ${RIO_USER:=lvuser}
: ${RIO_PORT:=22}
RIO=${RIO_USER}@${RIO_HOST}

PROJECT_NAME=RoTator
RIO_JAVA=/usr/local/frc/JRE/bin/java
RIO_JAR_PATH=/home/lvuser/FRCUserProgram.jar
RIO_CONFIG_PATH=/home/lvuser/
RIO_CLEAN_FILES="/home/lvuser/config $RIO_JAR_PATH"
RIO_NETCONSOLE_COMMAND="env LD_LIBRARY_PATH=/usr/local/frc/rpath-lib/ /usr/local/frc/bin/netconsole-host"
RIO_DEBUG_PORT=8348

: ${SSH:=$(which ssh)}
: ${SCP:=$(which scp)}
#SSHFLAGS="-o ControlMaster=yes -o ControlPath=~/.ssh/controlmasters/$RIO $SSHFLAGS"
: ${GRADLE:=./gradlew}
GRADLEFLAGS="--offline $GRADLEFLAGS"

run_ssh() {
    $SSH $SSHFLAGS $RIO -p $RIO_PORT "$@"
}

run_scp() {
    $SCP $SSHFLAGS -P $RIO_PORT "$@" || { err "Error copying file over ssh. Is the RIO connected?"; exit 1; }
}

run_gradle() {
	$GRADLE $GRADLEFLAGS $@
}

#
# Makes a java flags string debugging on the roboRio
# @arg $1 Whether the program should suspend before debugging (y or n). No by default
#
rio_debug_flags() {
	echo "-XX:+UsePerfData -agentlib:jdwp=transport=dt_socket,address=$RIO_DEBUG_PORT,server=y,suspend=${1:-n}"
}

clean() {
	log "Removing deployed files from roboRIO"
    run_ssh "rm -rf $RIO_CLEAN_FILES"
}

java_check() {
	if ! run_ssh "$RIO_JAVA -version > /dev/null"; then
        err "No java appears to be present on the roboRIO at $RIO_JAVA"
        exit 1
	fi
}

deploy_jar() {
    log "Building JAR file with gradle"
    run_gradle :rio:jar
    log "Deploying build JAR to $RIO:$RIO_JAR_PATH"
    run_scp build/libs/RoTator.jar $RIO:$RIO_JAR_PATH
    log "Deployed JAR file"
}

deploy_config() {
    log "Deploying config to $RIO:$RIO_CONFIG_PATH"
    run_scp -r ./config/ $RIO:$RIO_CONFIG_PATH
    log "Deployed config to rio"
}

deploy_robotCommand() {
    run_ssh echo "$@" > /home/lvuser/robotCommand
}

netconsole_command() {
    echo $RIO_NETCONSOLE_COMMAND $@
}

profile_run() {
    deploy_robotCommand $(netconsole_command $RIO_JAVA -jar $RIO_JAR_PATH)
}

profile_debug() {
    deploy_robotCommand $(netconsole_command $RIO_JAVA $(rio_debug_flags n) -jar $RIO_JAR_PATH)
}

profile_debugSuspend() {
    deploy_robotCommand $(netconsole_command $RIO_JAVA $(rio_debug_flags y) -jar $RIO_JAR_PATH)
}

reboot() {
    run_ssh /sbin/reboot
    log "roboRIO is rebooting"
}

restart() {
    log "Restarting robot code"
    run_ssh "killall -q netconsole-host || :"
    run_ssh ". /etc/profile.d/natinst-path.sh; /usr/local/frc/bin/frcKillRobot.sh -t -r"
}

execute() {
    log "Executing robot program on roboRIO"
    run_ssh ". /etc/profile.d/natinst-path.sh; /usr/local/frc/bin/frcKillRobot.sh -t"
    run_ssh "killall -q netconsole-host || :"
    run_ssh "killall -q java || :"
    run_ssh "sh robotCommand"
}

help() {
    cat <<EOF
$0 - deploy script for $PROJECT_NAME
Usage: $0 [command ...]

Commands:
    
EOF
}

if [[ $# == 0 ]]; then
    help
    exit 1
fi

while [[ $# > 0 ]]; do
    case $1 in
        clean) clean ;;
        java_check) java_check ;;
        deploy_jar) deploy_jar ;;
        deploy_config) deploy_config ;;
        profile_run) profile_run ;;
        profile_debug) profile_debug ;;
        profile_debugSuspend) profile_debugSuspend ;;
        reboot) reboot ;;
        restart) restart ;;
        execute) execute ;;
        help) help ;;
        *) err "Invalid command $1"; help ;;
    esac
    shift
done
