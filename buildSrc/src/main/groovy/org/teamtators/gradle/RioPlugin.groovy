package org.teamtators.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.internal.impldep.org.mortbay.jetty.servlet.SessionHandler
import org.hidetake.groovy.ssh.Ssh
import org.hidetake.groovy.ssh.core.Remote
import org.hidetake.groovy.ssh.session.BadExitStatusException
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class RioPlugin implements Plugin<Project> {
    static final Logger logger = LoggerFactory.getLogger(RioPlugin.class)
    static final String remoteJavaPath = "/usr/local/frc/JRE/bin/java"
    static final String remoteJarPath = "/home/lvuser/FRCUserProgram.jar"
    static final String remoteConfigPath = "/home/lvuser/"
    static final String remoteCleanFiles = "/home/lvuser/config /home/lvuser/FRCUserProgram.jar"
    static
    final String netConsoleCommand = "env LD_LIBRARY_PATH=/usr/local/frc/rpath-lib/ /usr/local/frc/bin/netconsole-host"
    static final int debugPort = 8348

    final def ssh = Ssh.newService()

    def Project project

    static String debugFlags(Boolean suspend = true) {
        def suspendFlag = suspend ? 'y' : 'n'
        "-XX:+UsePerfData -agentlib:jdwp=transport=dt_socket,address=${debugPort},server=y,suspend=${suspendFlag}"
    }

    static String getRobotCommand(String debugFlags = "") {
        "${netConsoleCommand} ${remoteJavaPath} ${debugFlags} -jar ${remoteJarPath}"
    }

    void apply(Project project) {
        this.project = project

        project.apply plugin: 'org.hidetake.ssh'
        project.extensions.create("rio", RioPluginExtension)

        project.task('rioClean', group: 'rio',
                description: 'Cleans deployed files from the robot') << {
            inSession {
                rioClean()
            }
        }

        project.task('rioJavaCheck', group: 'rio',
                description: 'Checks that the robot has the JRE installed at the correct location') << {
            inSession {
                rioJavaCheck()
            }
        }

        project.task('deployJar', group: 'rio', dependsOn: project.jar,
                description: 'Deploys the compiled code in a JAR file to the robot') << {
            inSession {
                deployJar()
            }
        }

        project.task('deployConfig', group: 'rio',
                description: 'Deploys configs to the robot') << {
            inSession {
                deployConfig()
            }
        }

        project.task('rioReboot', group: 'rio',
                description: 'Reboots the roboRIO') << {
            inSession {
                rioReboot()
            }
        }

        project.task('rioRestart', group: 'rio',
                description: 'Restarts the robot code on the roboRIO') << {
            inSession {
                rioRestart()
            }
        }

        project.task('rioProfileRun', group: 'rio',
                description: 'Sets the rio for running the robot code normally') << {
            inSession {
                rioProfileRun()
            }
        }

        project.task('rioProfileDebug', group: 'rio',
                description: 'Sets the rio for debugging the robot code') << {
            inSession {
                rioProfileDebug()
            }
        }

        project.task('rioProfileDebugSuspend', group: 'rio',
                description: 'Sets the rio for debugging the robot code, suspending startup') << {
            inSession {
                rioProfileDebugSuspend()
            }
        }


        project.task('deploy', group: 'rio', dependsOn: project.jar,
                description: 'Deploys configs, robot code and restarts the robot code on the roboRIO') << {
            inSession {
                deployConfig()
                deployJar()
                rioProfileRun()
                rioRestart()
            }
        }

        project.task('rioDebug', group: 'rio', dependsOn: project.jar,
                description: 'Sets up the roboRIO for remote debugging the robot code') << {
            inSession {
                deployConfig()
                deployJar()
                rioProfileDebug()
                rioRestart()
            }
        }

        project.task('rioDebugSuspend', group: 'rio', dependsOn: project.jar,
                description: 'Sets up the roboRIO for remote debugging the robot code, suspending startup') << {
            inSession {
                deployConfig()
                deployJar()
                rioProfileDebugSuspend()
                rioRestart()
            }
        }
    }

    private void rioProfileDebugSuspend() {
        println('Setting rio profile to debug w/ suspend')
        setRobotCommand(getRobotCommand(debugFlags(true)))
    }

    private void rioProfileDebug() {
        println('Setting rio profile to debug')
        setRobotCommand(getRobotCommand(debugFlags(false)))
    }

    private void rioProfileRun() {
        println('Setting rio profile to run')
        setRobotCommand(getRobotCommand())
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
        println("Cleaning deployed robot files")
        execute "rm -rf ${remoteCleanFiles}"
    }

    void rioJavaCheck() {
        try {
            execute "${remoteJavaPath} -version > /dev/null"
        } catch (BadExitStatusException e) {
            logger.warn("There is no valid Java Runtime Environment installed on the roboRIO", e)
        }
    }

    void deployJar() {
        println("Deploying JAR file to ${remoteJarPath}")
        put from: project.jar.archivePath, into: remoteJarPath
        execute "sync"
        println("Deploying JAR successfully")
    }

    void deployConfig() {
        println("Deploying config to ${remoteConfigPath}")
        put from: project.getRootProject().file('./config'), into: remoteConfigPath
        execute "sync"
        println("Deploying config successfully")
    }

    void rioReboot() {
        println("Rebooting roboRIO")
        execute "/sbin/reboot"
        println("RoboRIO is rebooting")
    }

    void rioRestart() {
        println("Restarting code on roboRIO")
        execute "killall -q netconsole-host || :"
        execute ". /etc/profile.d/natinst-path.sh; /usr/local/frc/bin/frcKillRobot.sh -t -r"
        println("Robot code is restarting")
    }

    void setRobotCommand(String robotCommand) {
        execute "echo \"${robotCommand}\" > /home/lvuser/robotCommand"
    }
}
