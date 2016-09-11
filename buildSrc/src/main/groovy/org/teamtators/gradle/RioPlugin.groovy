package org.teamtators.gradle

import groovy.util.logging.Slf4j
import org.apache.log4j.LogManager
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.internal.impldep.org.mortbay.jetty.servlet.SessionHandler
import org.hidetake.groovy.ssh.Ssh
import org.hidetake.groovy.ssh.core.Remote
import org.hidetake.groovy.ssh.session.BadExitStatusException

class RioPlugin implements Plugin<Project> {
    static final def logger = LogManager.getLogger(RioPlugin.class)
    static final def remoteJavaPath = "/usr/local/frc/JRE/bin/java"
    static final def remoteJarPath = "/home/lvuser/FRCUserProgram.jar"
    static final def remoteConfigPath = "/home/lvuser/"
    static final def remoteCleanFiles = "/home/lvuser/config /home/lvuser/FRCUserProgram.jar"
    static final def netConsoleCommand = "env LD_LIBRARY_PATH=/usr/local/frc/rpath-lib/ /usr/local/frc/bin/netconsole-host"
    static final def debugFlags = "-XX:+UsePerfData -agentlib:jdwp=transport=dt_socket,address=8348,server=y,suspend=y"
    final def ssh = Ssh.newService()

    def Project project

    void apply(Project project) {
        this.project = project

        project.apply plugin: 'org.hidetake.ssh'
        project.extensions.create("rio", RioPluginExtension)

        project.task('rioClean', group: 'rio',
                description: 'Cleans deployed files from the robot') << {
            rioClean()
        }

        project.task('rioJavaCheck', group: 'rio',
                description: 'Checks that the robot has the JRE installed at the correct location') << {
            rioJavaCheck()
        }

        project.task('deployJar', group: 'rio', dependsOn: project.jar,
                description: 'Deploys the compiled code in a JAR file to the robot') << {
            deployJar()
        }

        project.task('deployConfig', group: 'rio',
                description: 'Deploys configs to the robot') << {
            deployConfig()
        }

        project.task('rioReboot', group: 'rio',
                description: 'Reboots the roboRIO') << {
            rioReboot()
        }

        project.task('rioRestart', group: 'rio',
                description: 'Restarts the robot code on the roboRIO') << {
            rioRestart()
        }

        project.task('rioProfileRun', group: 'rio',
                description: 'Sets the rio for running the robot code normally') << {
            rioProfileRun()
        }

        project.task('rioProfileDebug', group: 'rio',
                description: 'Sets the rio for debugging the robot code') << {
            rioProfileDebug()
        }

        project.task('deploy', group: 'rio', dependsOn: ['deployConfig', 'deployJar', 'rioProfileRun', 'rioRestart'],
                description: 'Deploys configs, robot code and restarts the robot code on the roboRIO')

        project.task('rioDebug', group: 'rio', dependsOn: ['deployConfig', 'deployJar', 'rioProfileDebug', 'rioRestart'],
                description: 'Sets up the roboRIO for remote debugging the robot code')
    }

    private int getTeamNumber() {
        project.rio.teamNumber
    }

    static <T, U> T callWithDelegate(@DelegatesTo(U) Closure<T> closure, U delegate, ... arguments) {
        def cloned = closure.clone() as Closure<T>
        cloned.resolveStrategy = Closure.DELEGATE_FIRST
        cloned.delegate = delegate
        cloned.call(*arguments)
    }

    static Remote newRemote(String name, @DelegatesTo(Remote) Closure closure) {
        def remote = new Remote(name)
        callWithDelegate(closure, remote)
        remote
    }

    private Remote getRemote() {
        def plugin = this;
        project.rio?.remote ?:
                newRemote('rio') {
                    host = plugin.host
                    user = plugin.user
                    password = ''
                    knownHosts = allowAnyHosts
                }
    }

    private String getUser() {
        project.rio?.user ?: 'lvuser'
    }

    private String getHost() {
        project.rio?.host ?: "roboRIO-$teamNumber-FRC.local"
    }

    private void inSession(@DelegatesTo(SessionHandler) Closure closure) {
        ssh.run {
            println("Connecting to rio remote ${remote}")
            session(remote, closure)
        }
    }

    void rioClean() {
        inSession {
            println("Cleaning deployed robot files")
            execute "rm -rf ${remoteCleanFiles}"
        }
    }

    void rioJavaCheck() {
        inSession {
            try {
                execute "${remoteJavaPath} -version > /dev/null"
            } catch (BadExitStatusException e) {
                logger.warn("There is no valid Java Runtime Environment installed on the roboRIO", e)
            }
        }
    }

    void deployJar() {
        inSession {
            println("Deploying JAR file to ${remoteJarPath}")
            put from: project.jar.archivePath, into: remoteJarPath
            execute "sync"
            println("Deploying JAR successfully")
        }
    }

    void deployConfig() {
        inSession {
            println("Deploying config to ${remoteConfigPath}")
            put from: project.getRootProject().file('./config'), into: remoteConfigPath
            execute "sync"
            println("Deploying config successfully")
        }
    }

    void rioReboot() {
        inSession {
            println("Rebooting roboRIO")
            execute "/sbin/reboot"
            println("RoboRIO is rebooting")
        }
    }

    void rioRestart() {
        inSession {
            println("Restarting code on roboRIO")
            execute "killall -q netconsole-host || :"
            execute ". /etc/profile.d/natinst-path.sh; /usr/local/frc/bin/frcKillRobot.sh -t -r"
            println("Robot code is restarting")
        }
    }

    void setRobotCommand(String robotCommand) {
        inSession {
            execute "echo \"${robotCommand}\" > /home/lvuser/robotCommand"
        }
    }

    void rioProfileRun() {
        println('Setting rio profile to run')
        setRobotCommand("${netConsoleCommand} ${remoteJavaPath} -jar ${remoteJarPath}")
    }

    void rioProfileDebug() {
        println('Setting rio profile to debug')
        setRobotCommand("${netConsoleCommand} ${remoteJavaPath} ${debugFlags} -jar ${remoteJarPath}")
    }
}
