package org.teamtators.gradle

import org.hidetake.groovy.ssh.core.Remote

class RioPluginExtension {
    int teamNumber
    String host = null
    String user = null
    Remote remote = null
}