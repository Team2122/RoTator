package org.teamtators.rotator;

import dagger.Component;

import javax.inject.Singleton;

@Component(modules = RioModule.class)
@Singleton
public interface RioRobot extends CoreRobot {
}
