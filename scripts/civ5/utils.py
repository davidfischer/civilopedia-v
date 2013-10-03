from .databases import DatabaseAdapter


SQL_FOREIGN_KEY_ON = '''
    PRAGMA foreign_keys = ON
'''
SQL_CREATE_METADATA = '''
    CREATE TABLE "android_metadata" ("locale" TEXT DEFAULT 'en_US')
'''
SQL_METADATA_INSERT = '''
    INSERT INTO "android_metadata" VALUES ('en_US')
'''
CREATE_TECHNOLOGIES_SQL = '''
    CREATE TABLE technology (
        _id TEXT PRIMARY KEY,
        name TEXT,
        civilopedia TEXT,
        help TEXT,
        quote TEXT,
        cost INTEGER,
        era TEXT
    )
'''
INSERT_TECHNOLOGIES_SQL = '''
    INSERT INTO technology
        (_id, name, civilopedia, help, quote, cost, era)
    VALUES(:_id, :name, :civilopedia, :help, :quote, :cost, :era)
'''
CREATE_UNITS_SQL = '''
    CREATE TABLE unit (
        _id TEXT PRIMARY KEY,
        name TEXT,
        civilopedia TEXT,
        help TEXT,
        strategy TEXT,
        cost INTEGER,
        faith_cost INTEGER,
        combat INTEGER,
        ranged_combat INTEGER,
        moves INTEGER,
        range INTEGER,
        technology TEXT NULL,
        type TEXT,
        FOREIGN KEY (technology) REFERENCES technology (_id)
    )
'''
INSERT_UNITS_SQL = '''
    INSERT INTO unit
        (_id, name, civilopedia, help, strategy, cost,
         faith_cost, combat, ranged_combat, moves, range,
         technology, type)
    VALUES(:_id, :name, :civilopedia, :help, :strategy, :cost,
           :faith_cost, :combat, :ranged_combat, :moves, :range,
           :technology, :type)
'''
CREATE_BUILDINGS_SQL = '''
    CREATE TABLE building (
        _id TEXT PRIMARY KEY,
        name TEXT,
        civilopedia TEXT,
        help TEXT,
        strategy TEXT,
        cost INTEGER,
        faith_cost INTEGER,
        maintenance INTEGER,
        technology TEXT,
        type TEXT,
        sort_order INT,
        FOREIGN KEY (technology) REFERENCES technology (_id)
    )
'''
INSERT_BUILDINGS_SQL = '''
    INSERT INTO building
        (_id, name, civilopedia, help, strategy, cost,
         faith_cost, maintenance, technology, type, sort_order)
    VALUES(:_id, :name, :civilopedia, :help, :strategy, :cost,
           :faith_cost, :maintenance, :technology, :type, :sort_order)
'''
CREATE_WONDERS_SQL = '''
    CREATE TABLE wonder (
        _id TEXT PRIMARY KEY,
        name TEXT,
        civilopedia TEXT,
        help TEXT,
        strategy TEXT,
        quote TEXT,
        cost INTEGER,
        type TEXT
    )
'''
INSERT_WONDERS_SQL = '''
    INSERT INTO wonder
        (_id, name, civilopedia, help, strategy, quote, cost, type)
    VALUES(:_id, :name, :civilopedia, :help, :strategy, :quote, :cost, :type)
'''
CREATE_RELIGION_SQL = '''
    CREATE TABLE religion (
        _id TEXT PRIMARY KEY,
        name TEXT,
        civilopedia TEXT,
        sort_order INT,
        type TEXT
    )
'''
INSERT_RELIGION_SQL = '''
    INSERT INTO religion
        (_id, name, civilopedia, sort_order, type)
    VALUES(:_id, :name, :civilopedia, :sort_order, :type)
'''
CREATE_POLICIES_SQL = '''
    CREATE TABLE policy (
        _id TEXT PRIMARY KEY,
        name TEXT,
        help TEXT,
        civilopedia TEXT,
        sort_order INT,
        type TEXT
    )
'''
INSERT_POLICIES_SQL = '''
    INSERT INTO policy
        (_id, name, help, civilopedia, sort_order, type)
    VALUES(:_id, :name, :help, :civilopedia, :sort_order, :type)
'''

CREATE_SQLS = (
    SQL_CREATE_METADATA,
    CREATE_TECHNOLOGIES_SQL,
    CREATE_UNITS_SQL,
    CREATE_BUILDINGS_SQL,
    CREATE_WONDERS_SQL,
    CREATE_RELIGION_SQL,
    CREATE_POLICIES_SQL,
)

INSERT_SQLS = (
    ('technology', INSERT_TECHNOLOGIES_SQL),
    ('units', INSERT_UNITS_SQL),
    ('buildings', INSERT_BUILDINGS_SQL),
    ('wonders', INSERT_WONDERS_SQL),
    ('religion', INSERT_RELIGION_SQL),
    ('policies', INSERT_POLICIES_SQL),
)


def write_database(filepath, data):
    db = DatabaseAdapter(filepath)
    with db.conn:
        db.conn.execute(SQL_FOREIGN_KEY_ON)

        for sql in CREATE_SQLS:
            db.conn.execute(sql)

        db.conn.execute(SQL_METADATA_INSERT)

        for key, sql in INSERT_SQLS:
            db.conn.executemany(sql, (d for d in data[key]))
