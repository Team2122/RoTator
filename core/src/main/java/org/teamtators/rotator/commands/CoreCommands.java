package org.teamtators.rotator.commands;

import org.teamtators.rotator.config.ConfigCommandStore;

public class CoreCommands {
    public static void register(ConfigCommandStore commandStore) {
        commandStore.registerClass(DriveTank.class);
        commandStore.registerClass(DriveRotate.class);
        commandStore.registerClass(DriveStraight.class);

        commandStore.registerClass(LogCommand.class);

        commandStore.registerClass(PickerPick.class);
        commandStore.registerClass(PickerSetPosition.class);

        commandStore.registerClass(TurretBumpHoodPosition.class);
        commandStore.registerClass(TurretBumpRotation.class);
        commandStore.registerClass(TurretHome.class);
        commandStore.registerClass(TurretSetHoodPosition.class);
        commandStore.registerClass(TurretSetWheelSpeed.class);
        commandStore.registerClass(TurretShoot.class);
        commandStore.registerClass(TurretPrep.class);

        commandStore.registerClass(WaitCommand.class);
        commandStore.registerClass(CommandChooser.class);
    }
}
