package com.shatteredpixel.shatteredpixeldungeon.custom.dict;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.Image;

public class DictSpriteSheet {
    public static Image createImage(int sheet){
        if(sheet<10000) {
            return new ItemSprite(sheet);
        }
        return miscImages(sheet);
    }
    
    public static Image miscImages(int sheet){
        switch (sheet){
            case AREA_SEWER:
                return new Image(Assets.Environment.TILES_SEWERS, 32, 128, 32, 32);
            case AREA_PRISON:
                return new Image(Assets.Environment.TILES_PRISON, 32, 224, 32, 32);
            case AREA_CAVE:
                return new Image(Assets.Environment.TILES_CAVES, 32, 128, 32, 32);
            case AREA_CITY:
                return new Image(Assets.Environment.TILES_CITY, 32, 224, 32, 32);
            case AREA_HALL:
                return new Image(Assets.Environment.TILES_HALLS, 128, 256, 32, 32);

            case BOSS_CHAPTER1:
                return new Image(Assets.Sprites.HANDCLAP, 806, 6, 18, 32);
            case BOSS_CHAPTER2:
                return new Image(Assets.Sprites.W, 3, 0, 17, 32);
            case BOSS_CHAPTER3:
                return new Image(Assets.Sprites.MUD, 1139, 10, 30, 32);
            case BOSS_CHAPTER4:
                return new Image(Assets.Sprites.MEPHISTO, 6, 0, 21, 32);
            case BOSS_CHAPTER5:
                return new Image(Assets.Sprites.YOG, 7, 8, 32, 32);

            case RAT:
                return new Image(Assets.Sprites.BUG, 0, 0, 19, 16);
            case GNOLL:
                return new Image(Assets.Sprites.INFANTRY, 741, 4, 25, 28);
            case SNAKE:
                return new Image(Assets.Sprites.BABYBUG, 1, 0, 14, 16);
            case ALBINO:
                return new Image(Assets.Sprites.BUG_A, 0, 0, 19, 16);
            case CRAB:
                return new Image(Assets.Sprites.HAUND, 3, 1, 28, 32);
            case SWARM:
                return new Image(Assets.Sprites.YOMA, 5, 0, 23, 32);
            case SLIME:
                return new Image(Assets.Sprites.SHILDED, 737, 3, 26, 29);
            case F_RAT:
                return new Image(Assets.Sprites.SPITTER, 6, 0, 23, 32);
            case GNOLL_DARTER:
                return new Image(Assets.Sprites.CROSSBOWMAN, 450, 0, 28, 32);
            case CAUSTIC_SLIME:
                return new Image(Assets.Sprites.SHILDED_L, 737, 3, 26, 29);
            case GREAT_CRAB:
                return new Image(Assets.Sprites.DIFENDER, 550, 9, 28, 32);

            case THIEF:
                return new Image(Assets.Sprites.GHOST1, 0, 0, 12, 13);
            case BANDIT:
                return new Image(Assets.Sprites.GHOST1, 0, 13, 12, 13);
            case SKELETON:
                return new Image(Assets.Sprites.BOMBTAILG, 0, 0, 12, 15);
            case DM100:
                return new Image(Assets.Sprites.A_MASTER1, 0, 0, 16, 14);
            case GUARD:
                return new Image(Assets.Sprites.S_CASTER, 0, 0, 12, 16);
            case NECROMANCER:
                return new Image(Assets.Sprites.SCOUT, 0, 0, 16, 16);
            case ROT_HEART:
                return new Image(Assets.Sprites.ROT_HEART, 0, 0, 16, 16);
            case ROT_LASHER:
                return new Image(Assets.Sprites.ROT_LASH, 0, 0, 12, 16);
            case NEW_FIRE_ELE:
                return new Image(Assets.Sprites.RAPTOR_FIRE, 168, 0, 12, 14);
            //case SPECTRAL_NECROMANCER:
                //return new Image(Assets.Sprites.NECRO, 0, 16, 16, 16);

            case BAT:
                return new Image(Assets.Sprites.BREAKER, 0, 0, 15, 15);
            case BRUTE:
                return new Image(Assets.Sprites.RIVENGER, 0, 0, 12, 16);
            case ARMORED_BRUTE:
                return new Image(Assets.Sprites.BRUTE, 0, 16, 12, 16);
            case SHAMAN:
                return new Image(Assets.Sprites.CASTER, 0, 0, 12, 15);
            case SPINNER:
                return new Image(Assets.Sprites.SPINNER, 0, 0, 16, 16);
            case DM200:
                return new Image(Assets.Sprites.S_GOLEM, 0, 0, 21, 18);
            case DM201:
                return new Image(Assets.Sprites.I_GOLEM, 0, 18, 21, 18);
            case PYLON:
                return new Image(Assets.Sprites.PYLON, 10, 0, 10, 20);

            case GHOUL:
                return new Image(Assets.Sprites.POSSESSED, 0, 0, 12, 14);
            case WARLOCK:
                return new Image(Assets.Sprites.THROWER, 0, 0, 12, 15);
            case MONK:
                return new Image(Assets.Sprites.ENRAGED, 0, 0, 15, 14);
            case SENIOR:
                return new Image(Assets.Sprites.ENRAGED, 0, 14, 15, 14);
            case GOLEM:
                return new Image(Assets.Sprites.GOLEM, 0, 0, 17, 19);
            case ELEMENTAL_FIRE:
                return new Image(Assets.Sprites.DRONE, 0, 0, 12, 14);
            case ELEMENTAL_FROST:
                return new Image(Assets.Sprites.DRONE, 336, 0, 12, 14);
            case ELEMENTAL_SHOCK:
                return new Image(Assets.Sprites.DRONE, 0, 14, 12, 14);
            case ELEMENTAL_CHAOS:
                return new Image(Assets.Sprites.DRONE, 168, 14, 12, 14);

            case RIPPER:
                return new Image(Assets.Sprites.SARKAZ_SWARDMAN, 0, 0, 15, 14);
            case SPAWNER:
                return new Image(Assets.Sprites.SPAWNER, 0, 0, 16, 16);
            case SUCCUBUS:
                return new Image(Assets.Sprites.LANCER, 0, 0, 12, 15);
            case EYE:
                return new Image(Assets.Sprites.A_MASTER2, 0, 0, 16, 18);
            case SCORPIO:
                return new Image(Assets.Sprites.SARKAZ_SNIPER, 0, 0, 18, 17);
            case AICDIC:
                return new Image(Assets.Sprites.SARKAZ_SNIPER_E, 0, 17, 18, 17);
            case LARVA:
                return new Image(Assets.Sprites.URSUS_INFANTRI, 36, 0, 12, 8);
            case FIST_1:
                return new Image(Assets.Sprites.BOSS_2, 0, 0, 24, 17);
            case FIST_2:
                return new Image(Assets.Sprites.BOSS_3, 0, 34, 24, 17);
            case FIST_3:
                return new Image(Assets.Sprites.MEPHI_SINGER, 0, 68, 24, 17);

            case FISH:
                return new Image(Assets.Sprites.TENTACLE, 0, 0, 12, 16);
            case STATUE:
                return new Image(Assets.Sprites.GUERRILLA, 0, 0, 12, 15);
            case ARMORED_STATUE:
                return new Image(Assets.Sprites.GUERRILLA, 24, 45, 12, 15);
            case MIMIC:
                return new Image(Assets.Sprites.MIMIC, 0, 0, 16, 16);
            case MIMIC_GOLDEN:
                return new Image(Assets.Sprites.MIMIC, 0, 16, 16, 16);
            case MIMIC_CRYSTAL:
                return new Image(Assets.Sprites.MIMIC, 0, 32, 16, 16);
            case WRAITH:
                return new Image(Assets.Sprites.LURKER, 0, 0, 14, 15);
            case BEE:
                return new Image(Assets.Sprites.BEE, 0, 0, 16, 16);

            case SAD_GHOST:
                return new Image(Assets.Sprites.GUARD, 0, 0, 14, 15);
            case WAND_MAKER:
                return new Image(Assets.Sprites.ACE, 0, 0, 12, 14);
            case BLACKSMITH:
                return new Image(Assets.Sprites.VULCAN, 0, 0, 13, 16);
            case IMP:
                return new Image(Assets.Sprites.IMP, 0, 0, 12, 14);
            case IMAGE:
                return new Image(Assets.Sprites.BLAZE, 0, 15, 12, 15);
            case PRISMATIC_IMAGE:
                return new Image(Assets.Sprites.BLAZE, 0, 15, 12, 15);
            case RAT_KING:
                return new Image(Assets.Sprites.AJIMU, 0, 17, 16, 17);
            case SHEEP:
                return new Image(Assets.Sprites.CIVILIAN, 0, 0, 16, 15);
            case HERO:
                return new Image(Assets.Sprites.RED, 0, 15, 12, 15);
            case TRAP_GREEN_RECT:
                return new Image(Assets.Environment.TERRAIN_FEATURES, 48, 64, 16, 16);
            case LOCKED_FLOOR:
                return new Image(Assets.Environment.TILES_CAVES, 64, 80, 16, 16);
            case CHASM:
                return new Image(Assets.Environment.TILES_CAVES, 48, 48, 16, 16);
            case BUFF_POSITIVE:
                return new Image(Assets.Interfaces.BUFFS_LARGE, 48 ,16, 16, 16);
            case BUFF_NEUTRAL:
                return new Image(Assets.Interfaces.BUFFS_LARGE, 112 ,32, 16, 16);
            case BUFF_NEGATIVE:
                return new Image(Assets.Interfaces.BUFFS_LARGE, 224 ,0, 16, 16);


        }
        return new ItemSprite(ItemSpriteSheet.SOMETHING);
    }




    public static final int AREA_SEWER      = 0 + 10000;
    public static final int AREA_PRISON     = 1 + 10000;
    public static final int AREA_CAVE       = 2 + 10000;
    public static final int AREA_CITY       = 3 + 10000;
    public static final int AREA_HALL       = 4 + 10000;
    public static final int LOCKED_FLOOR    = 11 + 10000;
    public static final int CHASM           = 12 + 10000;

    public static final int BOSS_CHAPTER1   = 100 + 10000;
    public static final int BOSS_CHAPTER2   = 101 + 10000;
    public static final int BOSS_CHAPTER3   = 102 + 10000;
    public static final int BOSS_CHAPTER4   = 103 + 10000;
    public static final int BOSS_CHAPTER5   = 104 + 10000;
    public static final int SENTINEL        = 105 + 10000;
    public static final int CENTURION       = 106 + 10000;
    public static final int BLOOD_MAGISTER  = 107 + 10000;
    public static final int FAUST           = 108 + 10000;
    public static final int EMPEROR_PURSUER = 109 + 10000;
    public static final int KALTSIT         = 110 + 10000;
    public static final int MON3TR          = 111 + 10000;

    public static final int RAT             = 200 + 10000;
    public static final int ALBINO          = 201 + 10000;
    public static final int GNOLL           = 202 + 10000;
    public static final int SNAKE           = 203 + 10000;
    public static final int CRAB            = 204 + 10000;
    public static final int SWARM           = 205 + 10000;
    public static final int SLIME           = 206 + 10000;
    public static final int CAUSTIC_SLIME   = 207 + 10000;
    public static final int F_RAT           = 208 + 10000;
    public static final int GNOLL_DARTER    = 209 + 10000;
    public static final int GREAT_CRAB      = 210 + 10000;

    public static final int THIEF           = 300 + 10000;
    public static final int BANDIT          = 301 + 10000;
    public static final int SKELETON        = 302 + 10000;
    public static final int GUARD           = 303 + 10000;
    public static final int DM100           = 304 + 10000;
    public static final int NECROMANCER     = 305 + 10000;
    public static final int ROT_HEART       = 306 + 10000;
    public static final int ROT_LASHER      = 307 + 10000;
    public static final int NEW_FIRE_ELE    = 308 + 10000;
    public static final int SPECTRAL_NECROMANCER = 309 + 10000;

    public static final int BAT             = 400 + 10000;
    public static final int BRUTE           = 401 + 10000;
    public static final int ARMORED_BRUTE   = 402 + 10000;
    public static final int SHAMAN          = 403 + 10000;
    public static final int SPINNER         = 404 + 10000;
    public static final int DM200           = 405 + 10000;
    public static final int DM201           = 406 + 10000;
    public static final int PYLON           = 407 + 10000;

    public static final int GHOUL           = 500 + 10000;
    public static final int ELEMENTAL_FIRE  = 501 + 10000;
    public static final int ELEMENTAL_FROST = 502 + 10000;
    public static final int ELEMENTAL_SHOCK = 503 + 10000;
    public static final int ELEMENTAL_CHAOS = 504 + 10000;
    public static final int WARLOCK         = 505 + 10000;
    public static final int MONK            = 506 + 10000;
    public static final int SENIOR          = 507 + 10000;
    public static final int GOLEM           = 508 + 10000;

    public static final int RIPPER          = 600 + 10000;
    public static final int SPAWNER         = 601 + 10000;
    public static final int SUCCUBUS        = 602 + 10000;
    public static final int EYE             = 603 + 10000;
    public static final int SCORPIO         = 604 + 10000;
    public static final int AICDIC          = 605 + 10000;
    public static final int LARVA           = 606 + 10000;
    public static final int FIST_1          = 607 + 10000;
    public static final int FIST_2          = 608 + 10000;
    public static final int FIST_3          = 609 + 10000;

    public static final int STATUE          = 700 + 10000;
    public static final int ARMORED_STATUE  = 701 + 10000;
    public static final int FISH            = 702 + 10000;
    public static final int MIMIC           = 703 + 10000;
    public static final int MIMIC_GOLDEN    = 704 + 10000;
    public static final int MIMIC_CRYSTAL   = 705 + 10000;
    public static final int WRAITH = 706 + 10000;
    public static final int BEE             = 707 + 10000;

    public static final int SAD_GHOST       = 800 + 10000;
    public static final int WAND_MAKER      = 801 + 10000;
    public static final int BLACKSMITH      = 802 + 10000;
    public static final int IMP             = 803 + 10000;
    public static final int IMAGE           = 804 + 10000;
    public static final int PRISMATIC_IMAGE = 805 + 10000;
    public static final int RAT_KING        = 806 + 10000;
    public static final int SHEEP           = 807 + 10000;
    public static final int DUMMY           = 808 + 10000;
    public static final int FIREWALL        = 809 + 10000;
    public static final int FROSTLEAF       = 810 + 10000;
    public static final int JESSICA         = 811 + 10000;
    public static final int LENS            = 812 + 10000;
    public static final int NPC_PHANTOM     = 813 + 10000;


    public static final int HERO            = 2000 + 10000;


    public static final int TRAP_GREEN_RECT = 3000 + 10000;


    public static final int BUFF_POSITIVE   = 4000 + 10000;
    public static final int BUFF_NEUTRAL    = 4001 + 10000;
    public static final int BUFF_NEGATIVE   = 4002 + 10000;

}
