package org.teamtators.gradle

import groovy.util.logging.Slf4j
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.internal.impldep.org.mortbay.jetty.servlet.SessionHandler
import org.hidetake.groovy.ssh.Ssh
import org.hidetake.groovy.ssh.core.Remote
import org.hidetake.groovy.ssh.session.BadExitStatusException

@Slf4j
class RioPlugin implements Plugin<Project> {
    static final def remoteJavaPath = "/usr/local/frc/JRE/bin/java"
    static final def remoteJarPath = "/home/lvuser/FRCUserProgram.jar"
    final def ssh = Ssh.newService()

    def Project project

    void apply(Project project) {
        this.project = project

        project.apply plugin: 'org.hidetake.ssh'
        project.extensions.create("rio", RioPluginExtension)

        project.task('javaCheck', group: 'rio',
                description: 'Checks that the robot has the JRE installed at the correct location') << {
            javaCheck()
        }

        project.task('deploy', group: 'rio', dependsOn: ':rio:jar',
                description: 'Deploys the compiled code in a JAR file to the robot') << {
            deploy()
        }
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
            session(remote, closure)
        }
    }

    void javaCheck() {
        inSession {
                logger.debug("Checking for a valid JRE on the roboRIO")
                try {
                    execute "${remoteJavaPath} -version"
                } catch (BadExitStatusException e) {
                    logger.warn("There is no valid Java Runtime Environment installed on the roboRIO", e)
                }
            }
        }
    }

    void deploy() {
        ssh.run {
            session(remote) {
                println("Sending jar file to ${remoteJarPath}")
                put from: jar.archivePath, into: remoteJarPath
            }
        }
    }
}
