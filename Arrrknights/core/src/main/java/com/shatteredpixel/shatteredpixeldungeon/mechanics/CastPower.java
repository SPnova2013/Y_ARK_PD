package com.shatteredpixel.shatteredpixeldungeon.mechanics;

/*
	Scales a single cast while it resolves, so one "cast" can be split into several
	weaker bursts without touching every wand implementation.

	Set it before running a wand's onZap and reset it right after: damage wands read
	`damage`, every wand's timed buffs are shortened by `duration`.
 */
public class CastPower {

	//multiplier for direct damage
	public static float damage = 1f;
	//multiplier for the duration of timed effects applied during the cast
	public static float duration = 1f;

	public static void reset() {
		damage = 1f;
		duration = 1f;
	}

	public static int applyDamage( int dmg ) {
		return damage == 1f ? dmg : Math.round(dmg * damage);
	}

	public static float applyDuration( float time ) {
		return duration == 1f ? time : time * duration;
	}
}
