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
          t.Type AS "_id",
          l1.Text AS "name",
          l2.Text AS "civilopedia",
          l3.Text AS "help",
          l4.Text AS "quote",
          Cost AS "cost",
          l5.Text AS "era"
        FROM Technologies t
          INNER JOIN Eras e ON e.Type = t.Era
          INNER JOIN locale.Language_en_US l1 ON l1.Tag = t.Description
          INNER JOIN locale.Language_en_US l2 ON l2.Tag = t.Civilopedia
          INNER JOIN locale.Language_en_US l3 ON l3.Tag = t.Help
          INNER JOIN locale.Language_en_US l4 ON l4.Tag = t.Quote
          INNER JOIN locale.Language_en_US l5 ON l5.Tag = e.Description
        ORDER BY Cost
    '''
    SQL_UNITS = '''
        SELECT
          u.Type AS "_id",
          l1.Text AS "name",
          l2.Text AS "civilopedia",
          l3.Text AS "help",
          l4.Text AS "strategy",
          u.Cost AS "cost",
          FaithCost AS "faith_cost",
          Combat AS "combat",
          RangedCombat AS "ranged_combat",
          Moves AS "moves",
          Range AS "range",
          PrereqTech AS "technology",
          CASE WHEN su.Type = 'SPECIALUNIT_PEOPLE' THEN "Great Person"
               WHEN l5.Tag IS NOT NULL THEN l5.Text
               WHEN ReligiousStrength > 0 THEN "Religious"
               ELSE "Starting" END AS "type"
        FROM Units u
          LEFT JOIN SpecialUnits su ON su.Type = u.Special
          LEFT JOIN Technologies t ON t.Type = u.PrereqTech
          LEFT JOIN Eras e ON t.Era = e.Type
          LEFT JOIN locale.Language_en_US l1 ON l1.Tag = u.Description
          LEFT JOIN locale.Language_en_US l2 ON l2.Tag = u.Civilopedia
          LEFT JOIN locale.Language_en_US l3 ON l3.Tag = u.Help
          LEFT JOIN locale.Language_en_US l4 ON l4.Tag = u.Strategy
          LEFT JOIN locale.Language_en_US l5 ON l5.Tag = e.Description
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
          b.Cost AS "cost",
          FaithCost AS "faith_cost",
          GoldMaintenance AS "maintenance",
          PrereqTech AS "technology",
          CASE WHEN l5.Tag IS NOT NULL THEN l5.Text
               WHEN bc.Type = 'BUILDINGCLASS_MONUMENT' THEN "Starting"
               ELSE "Religious" END AS "type",
          CASE WHEN bc.Type = 'BUILDINGCLASS_MONUMENT' THEN -1
               WHEN e.ID IS NOT NULL THEN e.ID
               ELSE -2 END AS "sort_order"   -- Religious first
        FROM Buildings b
          INNER JOIN BuildingClasses bc ON b.BuildingClass = bc.Type
          LEFT JOIN Technologies t ON t.Type = b.PrereqTech
          LEFT JOIN Eras e ON t.Era = e.Type
          LEFT JOIN locale.Language_en_US l5 ON l5.Tag = e.Description
          LEFT JOIN locale.Language_en_US l1 ON l1.Tag = b.Description
          LEFT JOIN locale.Language_en_US l2 ON l2.Tag = b.Civilopedia
          LEFT JOIN locale.Language_en_US l3 ON l3.Tag = b.Help
          LEFT JOIN locale.Language_en_US l4 ON l4.Tag = b.Strategy
        WHERE MaxPlayerInstances <> 1    -- National wonders
          AND MaxGlobalInstances <> 1    -- World wonders
        ORDER BY b.Cost
    '''
    SQL_WONDERS = '''
        SELECT
          b.Type AS "_id",
          l1.Text AS "name",
          l2.Text AS "civilopedia",
          l3.Text AS "help",
          l4.Text AS "strategy",
          l5.Text AS "quote",
          Cost AS "cost",
          CASE WHEN MaxPlayerInstances = 1 THEN "National Wonder" ELSE "World Wonder" END AS "type"
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
    SQL_RELIGION = '''
        SELECT
          b.Type AS "_id",
          l1.Text AS "name",
          l2.Text AS "civilopedia",
          (Reformation * 10000 + Enhancer * 1000 + Follower * 100 + Founder * 10 + Pantheon) AS "sort_order",
          CASE WHEN Pantheon = 1 THEN "Pantheon Beliefs"
               WHEN Founder = 1 THEN "Founder Beliefs"
               WHEN Follower = 1 THEN "Follower Beliefs"
               WHEN Enhancer = 1 THEN "Enhancer Beliefs"
               ELSE "Reformation Beliefs" END AS "type"
        FROM Beliefs b
          LEFT JOIN locale.Language_en_US l1 ON l1.Tag = b.ShortDescription
          LEFT JOIN locale.Language_en_US l2 ON l2.Tag = b.Description
        ORDER BY b.Type
    '''
    SQL_POLICIES = '''
        SELECT
          p.Type AS "_id",
          l1.Text AS "name",
          l2.Text AS "civilopedia",
          l4.Text AS "help",
          l3.Text as "type",
          CASE WHEN e.ID IS NOT NULL THEN e.ID
               ELSE 0 END AS "sort_order"
        FROM Policies p
          INNER JOIN PolicyBranchTypes pb ON pb.Type = p.PolicyBranchType
          LEFT JOIN Eras e
            ON pb.EraPrereq = e.Type   -- Ancient era policies have no prereq
          LEFT JOIN locale.Language_en_US l1 ON l1.Tag = p.Description
          LEFT JOIN locale.Language_en_US l2 ON l2.Tag = p.Civilopedia
          LEFT JOIN locale.Language_en_US l4 ON l4.Tag = p.Help
          LEFT JOIN locale.Language_en_US l3 ON l3.Tag = pb.Description
        ORDER BY sort_order, p.Type
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

    def get_religion(self):
        with self.conn:
            for row in self.conn.execute(self.SQL_RELIGION):
                yield dict(row)

    def get_policies(self):
        with self.conn:
            for row in self.conn.execute(self.SQL_POLICIES):
                yield dict(row)
