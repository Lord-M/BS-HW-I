package com.binary_studio.fleet_commander.core.ship;

import com.binary_studio.fleet_commander.core.common.PositiveInteger;
import com.binary_studio.fleet_commander.core.exceptions.InsufficientPowergridException;
import com.binary_studio.fleet_commander.core.exceptions.NotAllSubsystemsFitted;
import com.binary_studio.fleet_commander.core.ship.contract.ModularVessel;
import com.binary_studio.fleet_commander.core.subsystems.contract.AttackSubsystem;
import com.binary_studio.fleet_commander.core.subsystems.contract.DefenciveSubsystem;

public final class DockedShip implements ModularVessel {

	private String name;

	private PositiveInteger shieldHP;

	private PositiveInteger hullHP;

	private PositiveInteger powergridOutput;

	private PositiveInteger capacitorAmount;

	private PositiveInteger capacitorRechargeRate;

	private PositiveInteger speed;

	private PositiveInteger size;

	private AttackSubsystem attackSubsystem;

	private DefenciveSubsystem defenciveSubsystem;

	public static DockedShip construct(String name, PositiveInteger shieldHP, PositiveInteger hullHP,
			PositiveInteger powergridOutput, PositiveInteger capacitorAmount, PositiveInteger capacitorRechargeRate,
			PositiveInteger speed, PositiveInteger size) {
		return new DockedShip(name, shieldHP, hullHP, powergridOutput, capacitorAmount, capacitorRechargeRate, speed,
				size);
	}

	private DockedShip(String name, PositiveInteger shieldHP, PositiveInteger hullHP, PositiveInteger powergridOutput,
			PositiveInteger capacitorAmount, PositiveInteger capacitorRechargeRate, PositiveInteger speed,
			PositiveInteger size) {
		this.name = name;
		this.shieldHP = shieldHP;
		this.hullHP = hullHP;
		this.powergridOutput = powergridOutput;
		this.capacitorAmount = capacitorAmount;
		this.capacitorRechargeRate = capacitorRechargeRate;
		this.speed = speed;
		this.size = size;
	}

	@Override
	public void fitAttackSubsystem(AttackSubsystem subsystem) throws InsufficientPowergridException {
		var sysTotalPgConsumption = getTotalConsumption(subsystem, this.defenciveSubsystem);
		var bothSysFitShipPgOutput = checkSysTotalPgConsumption(sysTotalPgConsumption);

		if (bothSysFitShipPgOutput) {
			this.attackSubsystem = subsystem;
		}
		else {
			var missingPowergrid = sysTotalPgConsumption.value() - this.powergridOutput.value();
			throw new InsufficientPowergridException(missingPowergrid);
		}
	}

	@Override
	public void fitDefensiveSubsystem(DefenciveSubsystem subsystem) throws InsufficientPowergridException {
		var sysTotalPgConsumption = getTotalConsumption(this.attackSubsystem, subsystem);
		var bothSysFitShipPgOutput = checkSysTotalPgConsumption(sysTotalPgConsumption);

		if (bothSysFitShipPgOutput) {
			this.defenciveSubsystem = subsystem;
		}
		else {
			var missingPowergrid = sysTotalPgConsumption.value() - this.powergridOutput.value();
			throw new InsufficientPowergridException(missingPowergrid);
		}
	}

	public CombatReadyShip undock() throws NotAllSubsystemsFitted {
		if (this.attackSubsystem == null && this.defenciveSubsystem == null) {
			throw NotAllSubsystemsFitted.bothMissing();
		}
		else if (this.attackSubsystem == null) {
			throw NotAllSubsystemsFitted.attackMissing();
		}
		else if (this.defenciveSubsystem == null) {
			throw NotAllSubsystemsFitted.defenciveMissing();
		}
		return CombatReadyShip.construct(this.name, this.shieldHP, this.hullHP, this.powergridOutput,
				this.capacitorAmount, this.capacitorRechargeRate, this.speed, this.size, this.attackSubsystem,
				this.defenciveSubsystem);
	}

	private PositiveInteger getTotalConsumption(AttackSubsystem attackSubsystem,
			DefenciveSubsystem defenciveSubsystem) {
		var attackSysPgConsumption = (attackSubsystem == null) ? 0 : attackSubsystem.getPowerGridConsumption().value();
		var defenciveSysPgConsumption = (defenciveSubsystem == null) ? 0
				: defenciveSubsystem.getPowerGridConsumption().value();
		return PositiveInteger.of(attackSysPgConsumption + defenciveSysPgConsumption);
	}

	private boolean checkSysTotalPgConsumption(PositiveInteger sysTotalPgConsumption) {
		return sysTotalPgConsumption.value() <= this.powergridOutput.value();
	}

}
