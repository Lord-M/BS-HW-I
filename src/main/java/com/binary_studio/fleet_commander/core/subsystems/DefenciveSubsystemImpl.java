package com.binary_studio.fleet_commander.core.subsystems;

import com.binary_studio.fleet_commander.core.actions.attack.AttackAction;
import com.binary_studio.fleet_commander.core.actions.defence.RegenerateAction;
import com.binary_studio.fleet_commander.core.common.PositiveInteger;
import com.binary_studio.fleet_commander.core.subsystems.contract.DefenciveSubsystem;

public final class DefenciveSubsystemImpl implements DefenciveSubsystem {

	private final String name;

	private final PositiveInteger powergridConsumption;

	private final PositiveInteger capacitorConsumption;

	private final PositiveInteger impactReductionPercent;

	private final PositiveInteger shieldRegeneration;

	private final PositiveInteger hullRegeneration;

	public static DefenciveSubsystemImpl construct(String name, PositiveInteger powergridConsumption,
			PositiveInteger capacitorConsumption, PositiveInteger impactReductionPercent,
			PositiveInteger shieldRegeneration, PositiveInteger hullRegeneration) throws IllegalArgumentException {
		return new DefenciveSubsystemImpl(name, powergridConsumption, capacitorConsumption, impactReductionPercent,
				shieldRegeneration, hullRegeneration);
	}

	private DefenciveSubsystemImpl(String name, PositiveInteger powergridConsumption,
			PositiveInteger capacitorConsumption, PositiveInteger impactReductionPercent,
			PositiveInteger shieldRegeneration, PositiveInteger hullRegeneration) throws IllegalArgumentException {
		if (name == null || name.isBlank()) {
			throw new IllegalArgumentException("Name should be not null and not empty");
		}
		this.name = name;
		this.powergridConsumption = powergridConsumption;
		this.capacitorConsumption = capacitorConsumption;
		this.impactReductionPercent = impactReductionPercent;
		this.shieldRegeneration = shieldRegeneration;
		this.hullRegeneration = hullRegeneration;
	}

	@Override
	public PositiveInteger getPowerGridConsumption() {
		return this.powergridConsumption;
	}

	@Override
	public PositiveInteger getCapacitorConsumption() {
		return this.capacitorConsumption;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public AttackAction reduceDamage(AttackAction incomingDamage) {
		var maxImpactReductionPercents = 95;
		var impactReductionPercents = Math.min(this.impactReductionPercent.value(), maxImpactReductionPercents);

		double impactReduction = (double) impactReductionPercents / 100;
		double reducedDamage = (double) incomingDamage.damage.value() * (1.0 - impactReduction);
		double roundedReducedDamage = Math.ceil(reducedDamage);

		return new AttackAction(PositiveInteger.of((int) roundedReducedDamage), incomingDamage.attacker,
				incomingDamage.target, incomingDamage.weapon);
	}

	@Override
	public RegenerateAction regenerate() {
		return new RegenerateAction(this.shieldRegeneration, this.hullRegeneration);
	}

}
