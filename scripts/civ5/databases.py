import os
import sqlite3


DATABASES = {
    'Localization': 'Localization-Merged.db',
    'Gamedata': 'Civ5DebugDatabase.db',
}


class DatabaseAdapter(object):
    def __init__(self, filepath):
        self.filepath = filepath
        self.conn = sqlite3.connect(filepath)
        self.conn.row_factory = sqlite3.Row

    def __del__(self):
        self.close()

    def close(self):
        self.conn.close()


class GamedataDB(DatabaseAdapter):
    SQL_LOCALIZATION_ATTACH = '''
        ATTACH DATABASE ? AS 'locale'
    '''
    SQL_TECHNOLOGIES = '''
        SELECT
          Type AS "_id",
          l1.Text AS "name",
          l2.Text AS "civilopedia",
          l3.Text AS "help",
          l4.Text AS "quote",
          Cost AS "cost"
        FROM Technologies t
          INNER JOIN locale.Language_en_US l1 ON l1.Tag = t.Description
          INNER JOIN locale.Language_en_US l2 ON l2.Tag = t.Civilopedia
          INNER JOIN locale.Language_en_US l3 ON l3.Tag = t.Help
          INNER JOIN locale.Language_en_US l4 ON l4.Tag = t.Quote
        ORDER BY Cost
    '''
    SQL_UNITS = '''
        SELECT
          Type AS "_id",
          l1.Text AS "name",
          l2.Text AS "civilopedia",
          l3.Text AS "help",
          l4.Text AS "strategy",
          Cost AS "cost",
          FaithCost AS "faith_cost",
          Combat AS "combat",
          RangedCombat AS "ranged_combat",
          Moves AS "moves",
          Range AS "range"
        FROM Units u
          LEFT JOIN locale.Language_en_US l1 ON l1.Tag = u.Description
          LEFT JOIN locale.Language_en_US l2 ON l2.Tag = u.Civilopedia
          LEFT JOIN locale.Language_en_US l3 ON l3.Tag = u.Help
          LEFT JOIN locale.Language_en_US l4 ON l4.Tag = u.Strategy
        WHERE ShowInPedia = 1
        ORDER BY Cost
    '''
    SQL_BUILDINGS = '''
        SELECT
          b.Type AS "_id",
          l1.Text AS "name",
          l2.Text AS "civilopedia",
          l3.Text AS "help",
          l4.Text AS "strategy",
          Cost AS "cost",
          FaithCost AS "faith_cost",
          GoldMaintenance AS "maintenance"
        FROM Buildings b
          INNER JOIN BuildingClasses bc ON b.BuildingClass = bc.Type
          LEFT JOIN locale.Language_en_US l1 ON l1.Tag = b.Description
          LEFT JOIN locale.Language_en_US l2 ON l2.Tag = b.Civilopedia
          LEFT JOIN locale.Language_en_US l3 ON l3.Tag = b.Help
          LEFT JOIN locale.Language_en_US l4 ON l4.Tag = b.Strategy
        WHERE MaxPlayerInstances <> 1    -- National wonders
          AND MaxGlobalInstances <> 1    -- World wonders
        ORDER BY Cost
    '''
    SQL_WONDERS = '''
        SELECT
          b.Type AS "_id",
          l1.Text AS "name",
          l2.Text AS "civilopedia",
          l3.Text AS "help",
          l4.Text AS "strategy",
          l5.Text AS "quote",
          Cost AS "cost"
        FROM Buildings b
          INNER JOIN BuildingClasses bc ON b.BuildingClass = bc.Type
          LEFT JOIN locale.Language_en_US l1 ON l1.Tag = b.Description
          LEFT JOIN locale.Language_en_US l2 ON l2.Tag = b.Civilopedia
          LEFT JOIN locale.Language_en_US l3 ON l3.Tag = b.Help
          LEFT JOIN locale.Language_en_US l4 ON l4.Tag = b.Strategy
          LEFT JOIN locale.Language_en_US l5 ON l5.Tag = b.Quote
        WHERE MaxPlayerInstances = 1    -- National wonders
          OR MaxGlobalInstances = 1    -- World wonders
        ORDER BY Cost
    '''

    def __init__(self, filepath):
        super(GamedataDB, self).__init__(filepath)

        locale_db_path = os.path.join(os.path.dirname(filepath),
                                      DATABASES['Localization'])
        if not os.path.isfile(locale_db_path):
            raise Exception('Could not find database: {}'.format(
                locale_db_path))

        with self.conn:
            self.conn.execute(self.SQL_LOCALIZATION_ATTACH,
                              (locale_db_path, ))

    def get_technologies(self):
        with self.conn:
            for row in self.conn.execute(self.SQL_TECHNOLOGIES):
                yield dict(row)

    def get_units(self):
        with self.conn:
            for row in self.conn.execute(self.SQL_UNITS):
                yield dict(row)

    def get_buildings(self):
        with self.conn:
            for row in self.conn.execute(self.SQL_BUILDINGS):
                yield dict(row)

    def get_wonders(self):
        with self.conn:
            for row in self.conn.execute(self.SQL_WONDERS):
                yield dict(row)
