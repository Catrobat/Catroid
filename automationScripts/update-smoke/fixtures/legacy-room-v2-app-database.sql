-- Legacy Catroid Room app_database fixture for update smoke tests.
-- Represents schema version 2 with identity hash c60ebf67428479ff4619d56d1eb30d08.
-- The project_response table intentionally lacks the version 3 private column.
PRAGMA journal_mode=DELETE;
PRAGMA user_version=2;
CREATE TABLE IF NOT EXISTS `featured_project` (`id` TEXT NOT NULL, `project_id` TEXT NOT NULL, `project_url` TEXT NOT NULL, `name` TEXT NOT NULL, `author` TEXT NOT NULL, `featured_image` TEXT NOT NULL, PRIMARY KEY(`id`));
CREATE TABLE IF NOT EXISTS `project_category` (`type` TEXT NOT NULL, `name` TEXT NOT NULL, PRIMARY KEY(`type`));
CREATE TABLE IF NOT EXISTS `project_response` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, `author` TEXT NOT NULL, `description` TEXT NOT NULL, `version` TEXT NOT NULL, `views` INTEGER NOT NULL, `download` INTEGER NOT NULL, `flavor` TEXT NOT NULL, `tags` TEXT NOT NULL, `uploaded` INTEGER NOT NULL, `uploadedString` TEXT NOT NULL, `screenshotLarge` TEXT NOT NULL, `screenshotSmall` TEXT NOT NULL, `projectUrl` TEXT NOT NULL, `downloadUrl` TEXT NOT NULL, `fileSize` REAL NOT NULL, `categoryType` TEXT NOT NULL, PRIMARY KEY(`id`, `categoryType`));
CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY, identity_hash TEXT);
INSERT OR REPLACE INTO room_master_table (id, identity_hash) VALUES(42, 'c60ebf67428479ff4619d56d1eb30d08');
