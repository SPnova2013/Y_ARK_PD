/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2021 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon.items;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.Gunaccessories.Accessories;
import com.shatteredpixel.shatteredpixeldungeon.items.Gunaccessories.C_Mag;
import com.shatteredpixel.shatteredpixeldungeon.items.Gunaccessories.DotSight;
import com.shatteredpixel.shatteredpixeldungeon.items.Gunaccessories.GunScope;
import com.shatteredpixel.shatteredpixeldungeon.items.Gunaccessories.GunScope_II;
import com.shatteredpixel.shatteredpixeldungeon.items.Gunaccessories.Ironsight;
import com.shatteredpixel.shatteredpixeldungeon.items.Gunaccessories.Muzzlebrake;
import com.shatteredpixel.shatteredpixeldungeon.items.Skill.SK1.BookCamouflage;
import com.shatteredpixel.shatteredpixeldungeon.items.Skill.SK1.BookChainHook;
import com.shatteredpixel.shatteredpixeldungeon.items.Skill.SK1.BookCrimsonCutter;
import com.shatteredpixel.shatteredpixeldungeon.items.Skill.SK1.BookExecutionMode;
import com.shatteredpixel.shatteredpixeldungeon.items.Skill.SK1.BookFate;
import com.shatteredpixel.shatteredpixeldungeon.items.Skill.SK1.BookFierceGlare;
import com.shatteredpixel.shatteredpixeldungeon.items.Skill.SK1.BookFoodPrep;
import com.shatteredpixel.shatteredpixeldungeon.items.Skill.SK1.BookHikari;
import com.shatteredpixel.shatteredpixeldungeon.items.Skill.SK1.BookHotBlade;
import com.shatteredpixel.shatteredpixeldungeon.items.Skill.SK1.BookLive;
import com.shatteredpixel.shatteredpixeldungeon.items.Skill.SK1.BookPhantomMirror;
import com.shatteredpixel.shatteredpixeldungeon.items.Skill.SK1.BookPowerfulStrike;
import com.shatteredpixel.shatteredpixeldungeon.items.Skill.SK1.BookShinkageryu;
import com.shatteredpixel.shatteredpixeldungeon.items.Skill.SK1.BookSoul;
import com.shatteredpixel.shatteredpixeldungeon.items.Skill.SK1.BookSpreadSpores;
import com.shatteredpixel.shatteredpixeldungeon.items.Skill.SK1.BookTacticalChanting;
import com.shatteredpixel.shatteredpixeldungeon.items.Skill.SK1.BookThoughts;
import com.shatteredpixel.shatteredpixeldungeon.items.Skill.SK1.BookWhispers;
import com.shatteredpixel.shatteredpixeldungeon.items.Skill.SK1.BookWolfSpirit;
import com.shatteredpixel.shatteredpixeldungeon.items.Skill.SK1.Bookpanorama;
import com.shatteredpixel.shatteredpixeldungeon.items.Skill.SK2.BookBenasProtracto;
import com.shatteredpixel.shatteredpixeldungeon.items.Skill.SK2.BookChargingPS;
import com.shatteredpixel.shatteredpixeldungeon.items.Skill.SK2.BookCoverSmoke;
import com.shatteredpixel.shatteredpixeldungeon.items.Skill.SK2.BookDawn;
import com.shatteredpixel.shatteredpixeldungeon.items.Skill.SK2.BookDeepHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.Skill.SK2.BookDreamland;
import com.shatteredpixel.shatteredpixeldungeon.items.Skill.SK2.BookEmergencyDefibrillator;
import com.shatteredpixel.shatteredpixeldungeon.items.Skill.SK2.BookFlashShield;
import com.shatteredpixel.shatteredpixeldungeon.items.Skill.SK2.BookGenesis;
import com.shatteredpixel.shatteredpixeldungeon.items.Skill.SK2.BookJackinthebox;
import com.shatteredpixel.shatteredpixeldungeon.items.Skill.SK2.BookLandingStrike;
import com.shatteredpixel.shatteredpixeldungeon.items.Skill.SK2.BookMentalBurst;
import com.shatteredpixel.shatteredpixeldungeon.items.Skill.SK2.BookNervous;
import com.shatteredpixel.shatteredpixeldungeon.items.Skill.SK2.BookNeverBackDown;
import com.shatteredpixel.shatteredpixeldungeon.items.Skill.SK2.BookPredators;
import com.shatteredpixel.shatteredpixeldungeon.items.Skill.SK2.BookReflow;
import com.shatteredpixel.shatteredpixeldungeon.items.Skill.SK2.BookRockfailHammer;
import com.shatteredpixel.shatteredpixeldungeon.items.Skill.SK2.BookSpikes;
import com.shatteredpixel.shatteredpixeldungeon.items.Skill.SK2.BookWolfPack;
import com.shatteredpixel.shatteredpixeldungeon.items.Skill.SK2.Bookancientkin;
import com.shatteredpixel.shatteredpixeldungeon.items.Skill.SK3.BookEveryone;
import com.shatteredpixel.shatteredpixeldungeon.items.Skill.SK3.BookNigetRaid;
import com.shatteredpixel.shatteredpixeldungeon.items.Skill.SK3.BookSBurst;
import com.shatteredpixel.shatteredpixeldungeon.items.Skill.SK3.BookShadowAssault;
import com.shatteredpixel.shatteredpixeldungeon.items.Skill.SK3.BookSharpness;
import com.shatteredpixel.shatteredpixeldungeon.items.Skill.SK3.BookSoaringFeather;
import com.shatteredpixel.shatteredpixeldungeon.items.Skill.SK3.BookSun;
import com.shatteredpixel.shatteredpixeldungeon.items.Skill.SK3.BookTerminationT;
import com.shatteredpixel.shatteredpixeldungeon.items.Skill.SK3.BookTrueSilverSlash;
import com.shatteredpixel.shatteredpixeldungeon.items.Skill.SK3.BookYourWish;
import com.shatteredpixel.shatteredpixeldungeon.items.Skill.SkillBook;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClothArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.LeatherArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.MailArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.PlateArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ScaleArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.AlchemistsToolkit;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.AlchemyKit;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.CapeOfThorns;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.ChaliceOfBlood;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.CloakOfShadows;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.CustomeSet;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.DriedRose;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.EtherealChains;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.HornOfPlenty;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.IsekaiItem;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.LloydsBeacon;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.MasterThievesArmband;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.SandalsOfNature;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TalismanOfForesight;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TimekeepersHourglass;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.UnstableSpellbook;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.WoundsofWar;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Food;
import com.shatteredpixel.shatteredpixeldungeon.items.food.MysteryMeat;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Pasty;
import com.shatteredpixel.shatteredpixeldungeon.items.food.ingredients.Potato;
import com.shatteredpixel.shatteredpixeldungeon.items.food.ingredients.Ingredients;
import com.shatteredpixel.shatteredpixeldungeon.items.food.ingredients.Salt;
import com.shatteredpixel.shatteredpixeldungeon.items.food.ingredients.SugarFlower;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfExperience;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfFrost;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHaste;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfInvisibility;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfLevitation;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfLiquidFlame;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfMindVision;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfParalyticGas;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfPurity;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfStrength;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfToxicGas;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfAccuracy;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfAmplified;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfAssassin;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfCommand;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfDominate;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfElements;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfEnergy;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfEvasion;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfForce;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfFuror;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfHaste;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfMight;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfMistress;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfSharpshooting;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfSunLight;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfTenacity;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfWealth;
import com.shatteredpixel.shatteredpixeldungeon.items.ror2items.APRounds;
import com.shatteredpixel.shatteredpixeldungeon.items.ror2items.Aegis;
import com.shatteredpixel.shatteredpixeldungeon.items.ror2items.ArmorPlate;
import com.shatteredpixel.shatteredpixeldungeon.items.ror2items.Behemoth;
import com.shatteredpixel.shatteredpixeldungeon.items.ror2items.Crowbar;
import com.shatteredpixel.shatteredpixeldungeon.items.ror2items.FrostRelic;
import com.shatteredpixel.shatteredpixeldungeon.items.ror2items.Gasoline;
import com.shatteredpixel.shatteredpixeldungeon.items.ror2items.LightFluxPauldron;
import com.shatteredpixel.shatteredpixeldungeon.items.ror2items.LuckyLeaf;
import com.shatteredpixel.shatteredpixeldungeon.items.ror2items.OddOpal;
import com.shatteredpixel.shatteredpixeldungeon.items.ror2items.Perforator;
import com.shatteredpixel.shatteredpixeldungeon.items.ror2items.ROR2item;
import com.shatteredpixel.shatteredpixeldungeon.items.ror2items.Raincoat;
import com.shatteredpixel.shatteredpixeldungeon.items.ror2items.Recycler;
import com.shatteredpixel.shatteredpixeldungeon.items.ror2items.StunGrenade;
import com.shatteredpixel.shatteredpixeldungeon.items.ror2items.SymbioticScorpion;
import com.shatteredpixel.shatteredpixeldungeon.items.ror2items.TitanicKnurl;
import com.shatteredpixel.shatteredpixeldungeon.items.ror2items.TopazBrooch;
import com.shatteredpixel.shatteredpixeldungeon.items.ror2items.TougherTimes;
import com.shatteredpixel.shatteredpixeldungeon.items.ror2items.Transcendence;
import com.shatteredpixel.shatteredpixeldungeon.items.ror2items.TriTipDagger;
import com.shatteredpixel.shatteredpixeldungeon.items.ror2items.UnstableTeslaCoil;
import com.shatteredpixel.shatteredpixeldungeon.items.ror2items.WakeOfVultures;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfIdentify;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfLullaby;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMagicMapping;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMirrorImage;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRage;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRecharging;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRemoveCurse;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRetribution;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTerror;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTransmutation;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfWarp;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.Recycle;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.Runestone;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfAffection;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfAggression;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfAugmentation;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfBlast;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfBlink;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfClairvoyance;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfDeepenedSleep;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfDisarming;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfEnchantment;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfFlock;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfIntuition;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfShock;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlowStone;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfCorrosion;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfCorruption;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfDisintegration;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfFireblast;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfFrost;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfHallucination;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfLightning;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfLivingEarth;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfMagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfPrismaticLight;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfRegrowth;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfSilence;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfTransfusion;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfWarding;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.AssassinsBlade;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.BattleAxe;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.BladeDemon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.C1_9mm;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.CatGun;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.ChenSword;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.CrabGun;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Crossbow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.DP27;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Dagger;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Dirk;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.DivineAvatar;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.EX42;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Echeveria;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Enfild;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Enfild2;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Firmament;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Flag;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Decapitator;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.FlameKatana;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.FlametailSword;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.FolkSong;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.FreshInspiration;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Gamzashield;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Gauntlet;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Gluttony;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.GoldDogSword;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.ImageoverForm;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.KazemaruWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.KollamSword;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.LoneJourney;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MinosFury;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.NaginataAndFan;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Niansword;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.PatriotSpear;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.RhodesSword;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.SakuraSword;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.SanktaBet;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.ShadowFirmament;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Gloves;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.DeepAbyss;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Greatshield;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Greatsword;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Halberd;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.HandAxe;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.KRISSVector;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Longsword;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.M1887;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Suffering;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.ThermiteBlade;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MetallicUnion;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MidnightSword;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Naginata;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.NEARL_AXE;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.R4C;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.RadiantSpear;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.RoundShield;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.RunicBlade;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.SHISHIOH;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Sai;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Castlebreaker;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Scythe;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Shortsword;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.SnowHunter;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Spear;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Sword;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.SwordofArtorius;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Laevateinn;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Destreza;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Violin;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.WarJournalist;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.WintersScar;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.WornShortsword;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.Bolas;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ExplosiveSpear;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.FishingSpear;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ForceCube;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.HeavyBoomerang;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.Javelin;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.Kunai;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.LightKnife;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.Shuriken;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ThrowingClub;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ThrowingHammer;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ThrowingSpear;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ThrowingStone;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.Tomahawk;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.Trident;
import com.shatteredpixel.shatteredpixeldungeon.plants.Blindweed;
import com.shatteredpixel.shatteredpixeldungeon.plants.Dreamfoil;
import com.shatteredpixel.shatteredpixeldungeon.plants.Earthroot;
import com.shatteredpixel.shatteredpixeldungeon.plants.Fadeleaf;
import com.shatteredpixel.shatteredpixeldungeon.plants.Firebloom;
import com.shatteredpixel.shatteredpixeldungeon.plants.Icecap;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.plants.Rotberry;
import com.shatteredpixel.shatteredpixeldungeon.plants.Sorrowmoss;
import com.shatteredpixel.shatteredpixeldungeon.plants.Starflower;
import com.shatteredpixel.shatteredpixeldungeon.plants.Stormvine;
import com.shatteredpixel.shatteredpixeldungeon.plants.Sungrass;
import com.shatteredpixel.shatteredpixeldungeon.plants.Swiftthistle;
import com.watabou.utils.Bundle;
import com.watabou.utils.GameMath;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class Generator {

    public enum Category {
        WEAPON(4, MeleeWeapon.class),
        WEP_T1(0, MeleeWeapon.class),
        WEP_T2(0, MeleeWeapon.class),
        WEP_T3(0, MeleeWeapon.class),
        WEP_T4(0, MeleeWeapon.class),
        WEP_T5(0, MeleeWeapon.class),

        ARMOR(3, Armor.class),

        MISSILE(3, MissileWeapon.class),
        MIS_T1(0, MissileWeapon.class),
        MIS_T2(0, MissileWeapon.class),
        MIS_T3(0, MissileWeapon.class),
        MIS_T4(0, MissileWeapon.class),
        MIS_T5(0, MissileWeapon.class),

        WAND(2, Wand.class),
        RING(1, Ring.class),
        ARTIFACT(1, Artifact.class),

        FOOD(0, Food.class),

        POTION(16, Potion.class),
        SEED(2, Plant.Seed.class),

        SCROLL(16, Scroll.class),
        STONE(2, Runestone.class),

        GOLD(20, Gold.class),
        SKL_T1(0, SkillBook.class),
        SKL_T2(0, SkillBook.class),
        SKL_T3(0, SkillBook.class),
        SKL_RND(0, SkillBook.class),
        ACCESSORIES(0, Accessories.class),
        N_INGREDINETS(0,Ingredients.class),
        ROR2ITEM(1,ROR2item.class);

        public Class<?>[] classes;

        //some item types use a deck-based system, where the probs decrement as items are picked
        // until they are all 0, and then they reset. Those generator classes should define
        // defaultProbs. If defaultProbs is null then a deck system isn't used.
        //Artifacts in particular don't reset, no duplicates!
        public float[] probs;
        public float[] defaultProbs = null;

        public float prob;
        public Class<? extends Item> superClass;

        private Category(float prob, Class<? extends Item> superClass) {
            this.prob = prob;
            this.superClass = superClass;
        }

        public static int order(Item item) {
            for (int i = 0; i < values().length; i++) {
                if (values()[i].superClass.isInstance(item)) {
                    return i;
                }
            }

            return item instanceof Bag ? Integer.MAX_VALUE : Integer.MAX_VALUE - 1;
        }

        static {
            GOLD.classes = new Class<?>[]{
                    Gold.class};
            GOLD.probs = new float[]{1};

            POTION.classes = new Class<?>[]{
                    PotionOfStrength.class, //2 drop every chapter, see Dungeon.posNeeded()
                    PotionOfHealing.class,
                    PotionOfMindVision.class,
                    PotionOfFrost.class,
                    PotionOfLiquidFlame.class,
                    PotionOfToxicGas.class,
                    PotionOfHaste.class,
                    PotionOfInvisibility.class,
                    PotionOfLevitation.class,
                    PotionOfParalyticGas.class,
                    PotionOfPurity.class,
                    PotionOfExperience.class};
            POTION.defaultProbs = new float[]{0, 6, 4, 3, 3, 3, 2, 2, 2, 2, 2, 1};
            POTION.probs = POTION.defaultProbs.clone();

            SEED.classes = new Class<?>[]{
                    Rotberry.Seed.class, //quest item
                    Sungrass.Seed.class,
                    Fadeleaf.Seed.class,
                    Icecap.Seed.class,
                    Firebloom.Seed.class,
                    Sorrowmoss.Seed.class,
                    Swiftthistle.Seed.class,
                    Blindweed.Seed.class,
                    Stormvine.Seed.class,
                    Earthroot.Seed.class,
                    Dreamfoil.Seed.class,
                    Starflower.Seed.class};
            SEED.defaultProbs = new float[]{0, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 2};
            SEED.probs = SEED.defaultProbs.clone();

            SCROLL.classes = new Class<?>[]{
                    ScrollOfUpgrade.class, //3 drop every chapter, see Dungeon.souNeeded()
                    ScrollOfIdentify.class,
                    ScrollOfRemoveCurse.class,
                    ScrollOfMirrorImage.class,
                    ScrollOfRecharging.class,
                    ScrollOfTeleportation.class,
                    ScrollOfLullaby.class,
                    ScrollOfMagicMapping.class,
                    ScrollOfRage.class,
                    ScrollOfRetribution.class,
                    ScrollOfTerror.class,
                    ScrollOfTransmutation.class,
                    ScrollOfWarp.class
            };
            SCROLL.defaultProbs = new float[]{0, 6, 4, 3, 3, 3, 2, 2, 2, 2, 2, 1, 0};
            SCROLL.probs = SCROLL.defaultProbs.clone();

            STONE.classes = new Class<?>[]{
                    StoneOfEnchantment.class,   //1 is guaranteed to drop on floors 6-19
                    StoneOfIntuition.class,     //1 additional stone is also dropped on floors 1-3
                    StoneOfDisarming.class,
                    StoneOfFlock.class,
                    StoneOfShock.class,
                    StoneOfBlink.class,
                    StoneOfDeepenedSleep.class,
                    StoneOfClairvoyance.class,
                    StoneOfAggression.class,
                    StoneOfBlast.class,
                    StoneOfAffection.class,
                    StoneOfAugmentation.class  //1 is sold in each shop
            };
            STONE.defaultProbs = new float[]{1, 33, 33, 33, 33, 33, 33, 33, 33, 33, 33, 0};
            STONE.probs = STONE.defaultProbs.clone();

            WAND.classes = new Class<?>[]{
                    WandOfMagicMissile.class,
                    WandOfLightning.class,
                    WandOfDisintegration.class,
                    WandOfFireblast.class,
                    WandOfCorrosion.class,
                    WandOfBlastWave.class,
                    WandOfLivingEarth.class,
                    WandOfFrost.class,
                    WandOfPrismaticLight.class,
                    WandOfWarding.class,
                    WandOfTransfusion.class,
                    WandOfCorruption.class,
                    WandOfRegrowth.class,
                    WandOfSilence.class,
                    WandOfHealing.class,
                    WandOfHallucination.class,
                    WandOfBlowStone.class
            };
            WAND.probs = new float[]{3, 4, 4, 4, 3, 3, 3, 3, 3, 3, 3, 3, 2, 3, 2, 3, 2};

            //see generator.randomWeapon
            WEAPON.classes = new Class<?>[]{};
            WEAPON.probs = new float[]{};

            WEP_T1.classes = new Class<?>[]{
                    WornShortsword.class,
                    Gloves.class,
                    Dagger.class,
                    MagesStaff.class,
                    EX42.class,
                    NEARL_AXE.class,
                    ChenSword.class,
                    RhodesSword.class,
                    Violin.class,
                    FreshInspiration.class
            };
            WEP_T1.probs = new float[]{1, 1, 1, 0, 1, 1, 0, 0, 0, 0};

            WEP_T2.classes = new Class<?>[]{
                    Shortsword.class,
                    HandAxe.class,
                    Spear.class,
                    Dirk.class,
                    MidnightSword.class,
                    Halberd.class,
                    FlameKatana.class,
                    Enfild.class,
                    Firmament.class
            };
            WEP_T2.probs = new float[]{4, 5, 4, 4, 4, 5, 3, 0, 0};

            WEP_T3.classes = new Class<?>[]{
                    Sword.class,
                    ThermiteBlade.class,
                    Castlebreaker.class,
                    RoundShield.class,
                    Sai.class,
                    Destreza.class,
                    SHISHIOH.class,
                    Flag.class,
                    DP27.class,
                    C1_9mm.class,
                    Enfild2.class,
                    Gamzashield.class
            };
            WEP_T3.probs = new float[]{5, 5, 5, 4, 4, 4, 4, 4, 3, 3, 0, 0};

            WEP_T4.classes = new Class<?>[]{
                    Longsword.class,
                    BattleAxe.class,
                    RunicBlade.class,
                    AssassinsBlade.class,
                    Crossbow.class,
                    M1887.class,
                    Naginata.class,
                    Scythe.class,
                    FolkSong.class,
                    CrabGun.class,
                    SnowHunter.class,
                    FlametailSword.class,
                    MetallicUnion.class,
                    WarJournalist.class,
                    KazemaruWeapon.class,
                    NaginataAndFan.class,
                    BladeDemon.class,
                    Gluttony.class,
                    GoldDogSword.class,
                    SanktaBet.class
            };
            WEP_T4.probs = new float[]{4, 5, 4, 4, 3, 4, 4, 4, 3, 3, 2, 4, 2, 3, 4, 2, 0, 0, 0, 0};

            WEP_T5.classes = new Class<?>[]{
                    Greatsword.class,//4
                    Laevateinn.class,//3
                    ShadowFirmament.class,//3
                    DeepAbyss.class,//4
                    Greatshield.class,//4
                    Gauntlet.class,//4
                    Decapitator.class,//4
                    WintersScar.class,//3
                    SwordofArtorius.class,//3
                    DivineAvatar.class,//4
                    R4C.class,//4
                    RadiantSpear.class,//4
                    KRISSVector.class,//3
                    LoneJourney.class,//3
                    Echeveria.class,//3
                    Suffering.class,//3
                    CatGun.class,
                    ImageoverForm.class,
                    KollamSword.class,
                    MinosFury.class,
                    Niansword.class,
                    PatriotSpear.class,
                    SakuraSword.class
            };
            WEP_T5.probs = new float[]{4, 3, 3, 4, 4, 4, 4, 3, 3, 4, 4, 4, 3, 3, 3, 3, 0, 0, 0, 0, 0, 0, 0};//56

            //see Generator.randomArmor
            ARMOR.classes = new Class<?>[]{
                    ClothArmor.class,
                    LeatherArmor.class,
                    MailArmor.class,
                    ScaleArmor.class,
                    PlateArmor.class};
            ARMOR.probs = new float[]{0, 0, 0, 0, 0};

            //see Generator.randomMissile
            MISSILE.classes = new Class<?>[]{};
            MISSILE.probs = new float[]{};

            MIS_T1.classes = new Class<?>[]{
                    ThrowingStone.class,
                    LightKnife.class
            };
            MIS_T1.probs = new float[]{6,5};

            MIS_T2.classes = new Class<?>[]{
                    FishingSpear.class,
                    ThrowingClub.class,
                    Shuriken.class
            };
            MIS_T2.probs = new float[]{6, 5, 4};

            MIS_T3.classes = new Class<?>[]{
                    ThrowingSpear.class,
                    Kunai.class,
                    Bolas.class
            };
            MIS_T3.probs = new float[]{6, 5, 4};

            MIS_T4.classes = new Class<?>[]{
                    Javelin.class,
                    Tomahawk.class,
                    HeavyBoomerang.class
            };
            MIS_T4.probs = new float[]{6, 5, 4};

            MIS_T5.classes = new Class<?>[]{
                    Trident.class,
                    ThrowingHammer.class,
                    ForceCube.class,
                    ExplosiveSpear.class
            };
            MIS_T5.probs = new float[]{6, 5, 4, 2};

            FOOD.classes = new Class<?>[]{
                    Food.class,
                    Pasty.class,
                    MysteryMeat.class};
            FOOD.probs = new float[]{4, 1, 0};

            RING.classes = new Class<?>[]{
                    RingOfAccuracy.class,
                    RingOfEvasion.class,
                    RingOfElements.class,
                    RingOfForce.class,
                    RingOfFuror.class,
                    RingOfHaste.class,
                    RingOfEnergy.class,
                    RingOfMight.class,
                    RingOfSharpshooting.class,
                    RingOfTenacity.class,
                    RingOfWealth.class,
                    RingOfSunLight.class,
                    RingOfAmplified.class,
                    RingOfDominate.class,
                    RingOfAssassin.class,
                    RingOfMistress.class,
                    RingOfCommand.class
            };
            RING.probs = new float[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};

            ARTIFACT.classes = new Class<?>[]{
                    CapeOfThorns.class,
                    ChaliceOfBlood.class,
                    CloakOfShadows.class,
                    HornOfPlenty.class,
                    MasterThievesArmband.class,
                    SandalsOfNature.class,
                    TalismanOfForesight.class,
                    TimekeepersHourglass.class,
                    UnstableSpellbook.class,
                    AlchemistsToolkit.class,
                    DriedRose.class,
                    LloydsBeacon.class,
                    EtherealChains.class,
                    CustomeSet.class,
                    AlchemyKit.class,
                    IsekaiItem.class,
                    WoundsofWar.class
            };
            ARTIFACT.defaultProbs = new float[]{0, 1, 0, 1, 1, 1, 1, 1, 1, 0, 1, 0, 1, 1, 1, 1, 1};
            ARTIFACT.probs = ARTIFACT.defaultProbs.clone();

            SKL_T1.classes = new Class<?>[]{
                    BookFate.class,
                    Bookpanorama.class,
                    BookFoodPrep.class,
                    BookChainHook.class,
                    BookCrimsonCutter.class,
                    BookShinkageryu.class,
                    BookFierceGlare.class,
                    BookCamouflage.class,
                    BookWolfSpirit.class,
                    BookHotBlade.class,
                    BookSpreadSpores.class,
                    BookPhantomMirror.class,
                    BookLive.class,
                    BookSoul.class,
                    BookExecutionMode.class,
                    BookHikari.class,
                    BookPowerfulStrike.class,
                    BookTacticalChanting.class,
                    BookThoughts.class,
                    BookWhispers.class
            };
            SKL_T1.probs = new float[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0};

            SKL_T2.classes = new Class<?>[]{
                    BookJackinthebox.class,
                    BookRockfailHammer.class,
                    BookChargingPS.class,
                    BookNeverBackDown.class,
                    BookCoverSmoke.class,
                    BookBenasProtracto.class,
                    Bookancientkin.class,
                    BookFlashShield.class,
                    BookLandingStrike.class,
                    BookDreamland.class,
                    BookDeepHealing.class,
                    BookSpikes.class,
                    BookPredators.class,
                    BookDawn.class,
                    BookEmergencyDefibrillator.class,
                    BookGenesis.class,
                    BookMentalBurst.class,
                    BookNervous.class,
                    BookReflow.class,
                    BookWolfPack.class
            };
            SKL_T2.probs = new float[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0};

            SKL_T3.classes = new Class<?>[]{
                    BookTrueSilverSlash.class,
                    BookTerminationT.class,
                    BookEveryone.class,
                    BookSharpness.class,
                    BookNigetRaid.class,
                    BookSBurst.class,
                    BookShadowAssault.class,
                    BookSoaringFeather.class,
                    BookSun.class,
                    BookYourWish.class
            };
            SKL_T3.probs = new float[]{1, 1, 1, 1, 0, 0, 0, 0, 0, 0};


            SKL_RND.classes = new Class<?>[]{
                    BookFate.class, Bookpanorama.class, BookFoodPrep.class,
                    BookChainHook.class, BookCrimsonCutter.class, BookShinkageryu.class,  BookFierceGlare.class,
                    BookCamouflage.class, BookWolfSpirit.class, BookHotBlade.class, BookSpreadSpores.class,
                    BookPhantomMirror.class,  BookLive.class, BookSoul.class,

                    BookJackinthebox.class, BookRockfailHammer.class, BookChargingPS.class,
                    BookNeverBackDown.class, BookCoverSmoke.class, BookBenasProtracto.class, Bookancientkin.class,
                    BookLandingStrike.class, BookFlashShield.class, BookDreamland.class, BookDeepHealing.class, BookSpikes.class,
                    BookPredators.class,

                    BookTrueSilverSlash.class, BookTerminationT.class, BookEveryone.class, BookSharpness.class
            };
            SKL_RND.probs = new float[]{
                    4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4,
                    3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3,
                    3, 3, 3, 3
            };

            ACCESSORIES.classes = new Class<?>[]{
                    Ironsight.class,
                    DotSight.class,
                    Muzzlebrake.class,
                    GunScope.class,
                    GunScope_II.class,
                    C_Mag.class
            };
            ACCESSORIES.probs = new float[]{2, 3, 2, 2, 1, 2};


            N_INGREDINETS.classes = new Class<?>[]{
                    SugarFlower.class,
                    Salt.class,
                    Potato.class
            };
            N_INGREDINETS.probs = new float[]{1, 1,1};

            ROR2ITEM.classes = new Class<?>[]{
                    //white
                    APRounds.class,
                    ArmorPlate.class,
                    Crowbar.class,
                    Gasoline.class,
                    OddOpal.class,
                    StunGrenade.class,
                    TopazBrooch.class,
                    TougherTimes.class,
                    TriTipDagger.class,
                    //red
                    Aegis.class,
                    Behemoth.class,
                    LuckyLeaf.class,
                    Raincoat.class,
                    FrostRelic.class,
                    UnstableTeslaCoil.class,
                    SymbioticScorpion.class,
                    WakeOfVultures.class,
                    //yellow
                    Perforator.class,
                    TitanicKnurl.class,
                    //orange
                    Recycler.class,
                    //blue
                    Transcendence.class,
                    LightFluxPauldron.class,
                    //blue-e
                    //purple
                    };
            ROR2ITEM.defaultProbs = new float[]
                    {8, 8, 8, 8, 8, 8, 8, 8, 8,//white
                    1, 1, 1, 1, 1, 1, 1, 1,//red
                    2, 2,//yellow
                    3,//orange
                    2, 2,//blue
                    };
            ROR2ITEM.probs = ROR2ITEM.defaultProbs.clone();
        }
    }

    private static final float[][] floorSetTierProbs = new float[][]{
            {0, 75, 20, 4, 1},
            {0, 25, 50, 20, 5},
            {0, 0, 40, 50, 10},
            {0, 0, 20, 40, 40},
            {0, 0, 0, 20, 80}
    };

    private static HashMap<Category, Float> categoryProbs = new LinkedHashMap<>();

    public static void fullReset() {
        generalReset();
        for (Category cat : Category.values()) {
            reset(cat);
        }
    }

    public static void generalReset() {
        for (Category cat : Category.values()) {
            categoryProbs.put(cat, cat.prob);
        }
    }

    public static void reset(Category cat) {
        if (cat.defaultProbs != null) cat.probs = cat.defaultProbs.clone();
    }

    public static Item random() {
        Category cat = Random.chances(categoryProbs);
        if (cat == null) {
            generalReset();
            cat = Random.chances(categoryProbs);
        }
        categoryProbs.put(cat, categoryProbs.get(cat) - 1);
        return random(cat);
    }

    public static Item random(Category cat) {
        switch (cat) {
            case ARMOR:
                return randomArmor();
            case WEAPON:
                return randomWeapon();
            case MISSILE:
                return randomMissile();
            case ARTIFACT:
                Item item = randomArtifact();
                //if we're out of artifacts, return a ring instead.
                return item != null ? item : random(Category.RING);
            case ROR2ITEM:
                item = randomR2i();
                return item != null ? item : random(Category.ARTIFACT);
            default:
                int i = Random.chances(cat.probs);
                if (i == -1) {
                    reset(cat);
                    i = Random.chances(cat.probs);
                }
                if (cat.defaultProbs != null) cat.probs[i]--;
                return ((Item) Reflection.newInstance(cat.classes[i])).random();
        }
    }

    //overrides any deck systems and always uses default probs
    public static Item randomUsingDefaults(Category cat) {
        if (cat.defaultProbs == null) {
            return random(cat); //currently covers weapons/armor/missiles
        } else {
            return ((Item) Reflection.newInstance(cat.classes[Random.chances(cat.defaultProbs)])).random();
        }
    }

    public static Item random(Class<? extends Item> cl) {
        return Reflection.newInstance(cl).random();
    }

    public static Armor randomArmor() {
        return randomArmor(Dungeon.depth / 5);
    }

    public static Armor randomArmor(int floorSet) {

        floorSet = (int) GameMath.gate(0, floorSet, floorSetTierProbs.length - 1);

        Armor a = (Armor) Reflection.newInstance(Category.ARMOR.classes[Random.chances(floorSetTierProbs[floorSet])]);
        a.random();
        return a;
    }

    public static final Category[] wepTiers = new Category[]{
            Category.WEP_T1,
            Category.WEP_T2,
            Category.WEP_T3,
            Category.WEP_T4,
            Category.WEP_T5
    };

    public static MeleeWeapon randomWeapon() {
        return randomWeapon(Dungeon.depth / 5);
    }

    public static MeleeWeapon randomWeapon(int floorSet) {

        floorSet = (int) GameMath.gate(0, floorSet, floorSetTierProbs.length - 1);

        Category c = wepTiers[Random.chances(floorSetTierProbs[floorSet])];
        MeleeWeapon w = (MeleeWeapon) Reflection.newInstance(c.classes[Random.chances(c.probs)]);
        w.random();
        return w;
    }

    public static final Category[] misTiers = new Category[]{
            Category.MIS_T1,
            Category.MIS_T2,
            Category.MIS_T3,
            Category.MIS_T4,
            Category.MIS_T5
    };

    public static MissileWeapon randomMissile() {
        return randomMissile(Dungeon.depth / 5);
    }

    public static MissileWeapon randomMissile(int floorSet) {

        floorSet = (int) GameMath.gate(0, floorSet, floorSetTierProbs.length - 1);

        Category c = misTiers[Random.chances(floorSetTierProbs[floorSet])];
        MissileWeapon w = (MissileWeapon) Reflection.newInstance(c.classes[Random.chances(c.probs)]);
        w.random();
        return w;
    }

    //enforces uniqueness of artifacts throughout a run.
    public static Artifact randomArtifact() {

        Category cat = Category.ARTIFACT;
        int i = Random.chances(cat.probs);

        //if no artifacts are left, return null
        if (i == -1) {
            return null;
        }

        cat.probs[i]--;
        return (Artifact) Reflection.newInstance((Class<? extends Artifact>) cat.classes[i]).random();

    }

    public static boolean removeArtifact(Class<? extends Artifact> artifact) {
        Category cat = Category.ARTIFACT;
        for (int i = 0; i < cat.classes.length; i++) {
            if (cat.classes[i].equals(artifact) && cat.probs[i] > 0) {
                cat.probs[i] = 0;
                return true;
            }
        }
        return false;
    }

    public static ROR2item randomR2i() {

        Category cat = Category.ROR2ITEM;
        int i = Random.chances(cat.probs);

        if (i == -1) {
            return null;
        }

        cat.probs[i] = 0;
        return (ROR2item) Reflection.newInstance((Class<? extends ROR2item>) cat.classes[i]).random();

    }
    public static boolean removeR2i(Class<? extends ROR2item> ror2items) {
        Category cat = Category.ROR2ITEM;
        for (int i = 0; i < cat.classes.length; i++) {
            if (cat.classes[i].equals(ror2items) && cat.probs[i] > 0) {
                cat.probs[i] = 0;
                return true;
            }
        }
        return false;
    }

    private static final String GENERAL_PROBS = "general_probs";
    private static final String CATEGORY_PROBS = "_probs";

    public static void storeInBundle(Bundle bundle) {
        Float[] genProbs = categoryProbs.values().toArray(new Float[0]);
        float[] storeProbs = new float[genProbs.length];
        for (int i = 0; i < storeProbs.length; i++) {
            storeProbs[i] = genProbs[i];
        }
        bundle.put(GENERAL_PROBS, storeProbs);

        for (Category cat : Category.values()) {
            if (cat.defaultProbs == null) continue;
            boolean needsStore = false;
            for (int i = 0; i < cat.probs.length; i++) {
                if (cat.probs[i] != cat.defaultProbs[i]) {
                    needsStore = true;
                    break;
                }
            }

            if (needsStore) {
                bundle.put(cat.name().toLowerCase() + CATEGORY_PROBS, cat.probs);
            }
        }
    }

    public static void restoreFromBundle(Bundle bundle) {
        fullReset();

        if (bundle.contains(GENERAL_PROBS)) {
            float[] probs = bundle.getFloatArray(GENERAL_PROBS);
            for (int i = 0; i < probs.length; i++) {
                categoryProbs.put(Category.values()[i], probs[i]);
            }
        }

        for (Category cat : Category.values()) {
            if (bundle.contains(cat.name().toLowerCase() + CATEGORY_PROBS)) {
                float[] probs = bundle.getFloatArray(cat.name().toLowerCase() + CATEGORY_PROBS);
                if (cat.defaultProbs != null && probs.length == cat.defaultProbs.length) {
                    cat.probs = probs;
                }
            }
        }

        //pre-0.8.1
        if (bundle.contains("spawned_artifacts")) {
            for (Class<? extends Artifact> artifact : bundle.getClassArray("spawned_artifacts")) {
                Category cat = Category.ARTIFACT;
                for (int i = 0; i < cat.classes.length; i++) {
                    if (cat.classes[i].equals(artifact)) {
                        cat.probs[i] = 0;
                    }
                }
            }
        }

    }
}
