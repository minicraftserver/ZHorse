package com.github.zedd7.zhorse.enums;

public enum KeyWordEnum {

	/* general keywords */
	ADMIN_SUFFIX(".admin"),
	DOT("."),
	FREE_SUFFIX(".free"),
	ZH_PREFIX("zh."),

	/* config.yml keywords */
	ALLOW_FOAL_RIDING("Settings.allow-foal-riding"),
	ALLOW_LEASH_ON_UNDEAD_HORSE("Settings.allow-leash-on-undead-horse"),
	ALLOW_TAMING_OF_UNDEAD_HORSE("Settings.allow-taming-of-undead-horse"),
	AVAILABLE_LANGUAGES("Languages.available"),
	AUTO_ADMIN_SUFFIX(".auto-admin"),
	BANNED_NAMES("HorseNames.banned-names"),
	BLOCK_LEASHED_TELEPORT("Settings.block-leashed-teleport"),
	BLOCK_MOUNTED_TELEPORT("Settings.block-mounted-teleport"),
	CHECK_FOR_UPDATE("Settings.check-for-update"),
	CLAIMS_LIMIT_SUFFIX(".claims-limit"),
	CLAIM_ON_TAME("Settings.claim-on-tame"),
	COLOR_BYPASS_SUFFIX(".color-bypass"),
	COLOR_SUFFIX(".color"),
	COMMANDS("Commands"),
	COMMANDS_PREFIX("Commands."),
	COOLDOWN_SUFFIX(".cooldown"),
	COST_SUFFIX(".cost"),
	CROSSABLE_SUFFIX(".crossable"),
	DATABASE("Databases.mysql-config.database"),
	DATABASES("Databases"),
	DEFAULT_LANGUAGE("Languages.default"),
	DEFAULT_NAME("HorseNames.default-name"),
	DEFAULT_STABLE_LOCATION_WORLD("Settings.default-stable-location.world"),
	DEFAULT_STABLE_LOCATION_X("Settings.default-stable-location.x"),
	DEFAULT_STABLE_LOCATION_Y("Settings.default-stable-location.y"),
	DEFAULT_STABLE_LOCATION_Z("Settings.default-stable-location.z"),
	ENABLED_SUFFIX(".enabled"),
	FILE_NAME("Databases.sqlite-config.filename"),
	GIVE_RANDOM_NAMES("HorseNames.give-random-names"),
	GROUPS("Groups"),
	GROUPS_PREFIX("Groups."),
	HERE_MAX_RANGE("Settings.here-max-range"),
	HORSENAMES("HorseNames"),
	HOST("Databases.mysql-config.host"),
	LANGUAGES("Languages"),
	LOCK_ON_CLAIM("Settings.lock-on-claim"),
	MAXIMUM_LENGTH("HorseNames.maximum-length"),
	MINIMUM_LENGTH("HorseNames.minimum-length"),
	MUTE_CONSOLE("Settings.mute-console"),
	PASSWORD("Databases.mysql-config.password"),
	PERMISSION_SUFFIX(".permission"),
	PORT("Databases.mysql-config.port"),
	PROTECTIONS("Protections"),
	PROTECTIONS_PREFIX("Protections."),
	PROTECT_ON_CLAIM("Settings.protect-on-claim"),
	RANDOM_NAMES("HorseNames.random-names"),
	RESPAWN_MISSING_HORSE("Settings.respawn-missing-horse"),
	RESTORE_INVENTORY("Settings.restore-inventory"),
	REZ_STACK_SIZE("Settings.rez-stack-size"),
	SEND_TO_STABLE_ON_OWNER_LOGOUT("Settings.send-to-stable-on-owner-logout"),
	SETTINGS("Settings"),
	SHARE_ON_CLAIM("Settings.share-on-claim"),
	STABLE_MAX_RANGE("Settings.stable-max-range"),
	TABLE_PREFIX("Databases.mysql-config.table-prefix"),
	TP_MAX_RANGE("Settings.tp-max-range"),
	TYPE("Databases.type"),
	USE_PAPERAPI_TELEPORT_METHOD("Settings.use-paperapi-teleport-method"),
	USER("Databases.mysql-config.user"),
	USE_DEFAULT_STABLE("Settings.use-default-stable"),
	USE_EXACT_STATS("Settings.use-exact-stats"),
	USE_VANILLA_STATS("Settings.use-vanilla-stats"),
	WORLDS("Worlds"),
	WORLDS_PREFIX("Worlds."),

	/* locale_XX.yml keywords */
	AMOUNT_FLAG("<amount>"),
	CURRENCY_SYMBOL_FLAG("<currency>"),
	HORSE_NAME_FLAG("<horse>"),
	HORSE_ID_FLAG("<id>"),
	LANGUAGE_FLAG("<lang>"),
	MAX_FLAG("<max>"),
	PERMISSION_FLAG("<perm>"),
	PLAYER_NAME_FLAG("<player>"),
	VALUE_FLAG("<value>"),

	/* LocaleEnum keywords */
	DESCRIPTION("DESCRIPTION"),
	SEPARATOR("_"),
	USAGE("USAGE");

	private String value;

	KeyWordEnum(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
