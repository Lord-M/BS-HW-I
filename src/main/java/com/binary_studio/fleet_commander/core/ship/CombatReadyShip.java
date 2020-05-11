package com.binary_studio.fleet_commander.core.ship;

import java.util.Optional;

import com.binary_studio.fleet_commander.core.actions.attack.AttackAction;
import com.binary_studio.fleet_commander.core.actions.defence.AttackResult;
import com.binary_studio.fleet_commander.core.actions.defence.RegenerateAction;
import com.binary_studio.fleet_commander.core.common.Attackable;
import com.binary_studio.fleet_commander.core.common.PositiveInteger;
import com.binary_studio.fleet_commander.core.ship.contract.CombatReadyVessel;
import com.binary_studio.fleet_commander.core.subsystems.contract.AttackSubsystem;
import com.binary_studio.fleet_commander.core.subsystems.contract.DefenciveSubsystem;

public final class CombatReadyShip implements CombatReadyVessel {

	private String name;

	private PositiveInteger shieldHPMax;

	private PositiveInteger currentShieldHP;

	private PositiveInteger hullHPMax;

	private PositiveInteger currentHullHP;

	private PositiveInteger powergridOutput;

	private PositiveInteger capacitorAmountMax;

	private PositiveInteger currentCapacitorAmount;

	private PositiveInteger capacitorRechargeRate;

	private PositiveInteger speed;

	private PositiveInteger size;

	private AttackSubsystem attackSubsystem;

	private DefenciveSubsystem defenciveSubsystem;

	public static CombatReadyShip construct(String name, PositiveInteger shieldHP, PositiveInteger hullHP,
			PositiveInteger powergridOutput, PositiveInteger capacitorAmount, PositiveInteger capacitorRechargeRate,
			PositiveInteger speed, PositiveInteger size, AttackSubsystem attackSubsystem,
			DefenciveSubsystem defenciveSubsystem) {
		return new CombatReadyShip(name, shieldHP, hullHP, powergridOutput, capacitorAmount, capacitorRechargeRate,
				speed, size, attackSubsystem, defenciveSubsystem);
	}

	private CombatReadyShip(String name, PositiveInteger shieldHP, PositiveInteger hullHP,
			PositiveInteger powergridOutput, PositiveInteger capacitorAmount, PositiveInteger capacitorRechargeRate,
			PositiveInteger speed, PositiveInteger size, AttackSubsystem attackSubsystem,
			DefenciveSubsystem defenciveSubsystem) {
		this.name = name;
		this.shieldHPMax = shieldHP;
		this.currentShieldHP = shieldHP;
		this.hullHPMax = hullHP;
		this.currentHullHP = hullHP;
		this.powergridOutput = powergridOutput;
		this.capacitorAmountMax = capacitorAmount;
		this.currentCapacitorAmount = capacitorAmount;
		this.capacitorRechargeRate = capacitorRechargeRate;
		this.speed = speed;
		this.size = size;
		this.attackSubsystem = attackSubsystem;
		this.defenciveSubsystem = defenciveSubsystem;
	}

	@Override
	public void endTurn() {
		var rechargedCapacitorAmount = this.capacitorRechargeRate.value() + this.currentCapacitorAmount.value();
		var checkedCapacitorAmount = Math.min(rechargedCapacitorAmount, this.capacitorAmountMax.value());
		this.currentCapacitorAmount = PositiveInteger.of(checkedCapacitorAmount);
	}

	@Override
	public void startTurn() {
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public PositiveInteger getSize() {
		return this.size;
	}

	@Override
	public PositiveInteger getCurrentSpeed() {
		return this.speed;
	}

	@Override
	public Optional<AttackAction> attack(Attackable target) {
		var attackReqChargeAmount = this.attackSubsystem.getCapacitorConsumption().value();

		if (attackReqChargeAmount <= this.currentCapacitorAmount.value()) {
			this.currentCapacitorAmount = PositiveInteger
					.of(this.currentCapacitorAmount.value() - attackReqChargeAmount);
			var damage = this.attackSubsystem.attack(target);
			var attackAction = new AttackAction(damage, this, target, this.attackSubsystem);
			return Optional.of(attackAction);
		}
		else {
			return Optional.empty();
		}
	}

	@Override
	public AttackResult applyAttack(AttackAction attack) {
		var reducedAttack = this.defenciveSubsystem.reduceDamage(attack);
		var damage = reducedAttack.damage;

		var shieldHPRemaining = this.currentShieldHP.value() - damage.value();
		if (shieldHPRemaining >= 0) {
			this.currentShieldHP = PositiveInteger.of(shieldHPRemaining);
			return new AttackResult.DamageRecived(reducedAttack.weapon, damage, this);
		}

		var remainingDamageAmount = PositiveInteger.of(Math.abs(shieldHPRemaining));
		this.currentShieldHP = PositiveInteger.of(0);

		var hullHPRemaining = this.currentHullHP.value() - remainingDamageAmount.value();
		if (hullHPRemaining > 0) {
			this.currentHullHP = PositiveInteger.of(hullHPRemaining);
			return new AttackResult.DamageRecived(reducedAttack.weapon, damage, this);
		}

		return new AttackResult.Destroyed();
	}

	@Override
	public Optional<RegenerateAction> regenerate() {
		var defenseReqChargeAmount = this.defenciveSubsystem.getCapacitorConsumption().value();

		if (defenseReqChargeAmount <= this.currentCapacitorAmount.value()) {
			this.currentCapacitorAmount = PositiveInteger
					.of(this.currentCapacitorAmount.value() - defenseReqChargeAmount);
			var regenerateAction = this.defenciveSubsystem.regenerate();
			var hullRegeneratedAction = regenHull(regenerateAction);
			var shieldRegeneratedAction = regenShield(hullRegeneratedAction);
			return Optional.of(shieldRegeneratedAction);
		}
		else {
			return Optional.empty();
		}
	}

	private RegenerateAction regenHull(RegenerateAction regenerateAction) {

		var missingHullHP = this.hullHPMax.value() - this.currentHullHP.value();
		var availableHullRegenHP = regenerateAction.hullHPRegenerated;

		int regeneratedHullHP = 0;

		if (missingHullHP != 0) {
			regeneratedHullHP = (missingHullHP > availableHullRegenHP.value()) ? availableHullRegenHP.value()
					: missingHullHP;

			this.currentHullHP = PositiveInteger.of(regeneratedHullHP);
		}
		return createRegenerateAction(regenerateAction.shieldHPRegenerated, PositiveInteger.of(regeneratedHullHP));
	}

	private RegenerateAction regenShield(RegenerateAction regenerateAction) {

		var missingShieldHP = this.shieldHPMax.value() - this.currentShieldHP.value();
		var availableShieldRegenHP = regenerateAction.shieldHPRegenerated;

		int regeneratedShieldHP = 0;

		if (missingShieldHP != 0) {
			regeneratedShieldHP = (missingShieldHP > availableShieldRegenHP.value()) ? availableShieldRegenHP.value()
					: missingShieldHP;

			this.currentShieldHP = PositiveInteger.of(regeneratedShieldHP);
		}
		return createRegenerateAction(PositiveInteger.of(regeneratedShieldHP), regenerateAction.hullHPRegenerated);
	}

	private RegenerateAction createRegenerateAction(PositiveInteger shieldRegenerated,
			PositiveInteger hullRegenerated) {
		return new RegenerateAction(shieldRegenerated, hullRegenerated);
	}

}
