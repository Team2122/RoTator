package org.teamtators.rotator.commands;

import org.teamtators.rotator.config.ConfigCommandStore;

public class CoreCommands {
    public static void register(ConfigCommandStore commandStore) {
        commandStore.registerClass(DriveArcade.class);
        commandStore.registerClass(DriveTank.class);
        commandStore.registerClass(DriveRotate.class);
        commandStore.registerClass(DriveStraight.class);
        commandStore.registerClass(DriveStraightCheval.class);

        commandStore.registerClass(LogCommand.class);

        commandStore.registerClass(PickerBarf.class);
        commandStore.registerClass(PickerPick.class);
        commandStore.registerClass(PickerSetPosition.class);
        commandStore.registerClass(PickerCheval.class);

        commandStore.registerClass(TurretBumpHoodPosition.class);
        commandStore.registerClass(TurretBumpRotation.class);
        commandStore.registerClass(TurretBumpWheelSpeedOffset.class);
        commandStore.registerClass(TurretHome.class);
        commandStore.registerClass(TurretSetHoodPosition.class);
        commandStore.registerClass(TurretSetRotation.class);
        commandStore.registerClass(TurretSetWheelSpeed.class);
        commandStore.registerClass(TurretShoot.class);
        commandStore.registerClass(TurretTarget.class);
        commandStore.registerClass(TurretPrep.class);

        commandStore.registerClass(OIRumble.class);

        commandStore.registerClass(WaitCommand.class);
        commandStore.registerClass(ChooserCommand.class);
    }
}
