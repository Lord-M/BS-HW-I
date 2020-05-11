package com.binary_studio.fleet_commander.core.subsystems;

import com.binary_studio.fleet_commander.core.common.Attackable;
import com.binary_studio.fleet_commander.core.common.PositiveInteger;
import com.binary_studio.fleet_commander.core.subsystems.contract.AttackSubsystem;

public final class AttackSubsystemImpl implements AttackSubsystem {

	private final String name;

	private final PositiveInteger powergridRequirments;

	private final PositiveInteger capacitorConsumption;

	private final PositiveInteger optimalSpeed;

	private final PositiveInteger optimalSize;

	private final PositiveInteger baseDamage;

	public static AttackSubsystemImpl construct(String name, PositiveInteger powergridRequirments,
			PositiveInteger capacitorConsumption, PositiveInteger optimalSpeed, PositiveInteger optimalSize,
			PositiveInteger baseDamage) throws IllegalArgumentException {
		return new AttackSubsystemImpl(name, powergridRequirments, capacitorConsumption, optimalSpeed, optimalSize,
				baseDamage);
	}

	private AttackSubsystemImpl(String name, PositiveInteger powergridRequirments, PositiveInteger capacitorConsumption,
			PositiveInteger optimalSpeed, PositiveInteger optimalSize, PositiveInteger baseDamage)
			throws IllegalArgumentException {
		if (name == null || name.isBlank()) {
			// throw new IllegalArgumentException("Argument 'name' cannot be null or
			// empty");
			throw new IllegalArgumentException("Name should be not null and not empty");
		}

		this.name = name;
		this.powergridRequirments = powergridRequirments;
		this.capacitorConsumption = capacitorConsumption;
		this.optimalSpeed = optimalSpeed;
		this.optimalSize = optimalSize;
		this.baseDamage = baseDamage;
	}

	@Override
	public PositiveInteger getPowerGridConsumption() {
		return this.powergridRequirments;
	}

	@Override
	public PositiveInteger getCapacitorConsumption() {
		return this.capacitorConsumption;
	}

	@Override
	public PositiveInteger attack(Attackable target) {
		var targetSize = target.getSize().value();
		var optimalSize = this.optimalSize.value();

		var targetSpeed = target.getCurrentSpeed().value();
		var optimalSpeed = this.optimalSpeed.value();

		double sizeReductionModifier = (targetSize >= optimalSize) ? 1 : ((double) targetSize / optimalSize);
		double speedReductionModifier = (targetSpeed <= optimalSpeed) ? 1 : (double) optimalSpeed / (2 * targetSpeed);

		double damage = this.baseDamage.value() * Math.min(sizeReductionModifier, speedReductionModifier);
		double roundedDamage = Math.ceil(damage);

		return new PositiveInteger((int) roundedDamage);
	}

	@Override
	public String getName() {
		return this.name;
	}

}
