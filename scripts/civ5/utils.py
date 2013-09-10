from .databases import DatabaseAdapter


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
        cost INTEGER
    )
'''
INSERT_TECHNOLOGIES_SQL = '''
    INSERT INTO technology
        (_id, name, civilopedia, help, quote, cost)
    VALUES(:_id, :name, :civilopedia, :help, :quote, :cost)
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
        range INTEGER
    )
'''
INSERT_UNITS_SQL = '''
    INSERT INTO unit
        (_id, name, civilopedia, help, strategy, cost,
         faith_cost, combat, ranged_combat, moves, range)
    VALUES(:_id, :name, :civilopedia, :help, :strategy, :cost,
           :faith_cost, :combat, :ranged_combat, :moves, :range)
'''


def write_database(filepath, data):
    db = DatabaseAdapter(filepath)
    with db.conn:
        db.conn.execute(SQL_CREATE_METADATA)
        db.conn.execute(SQL_METADATA_INSERT)
        db.conn.execute(CREATE_TECHNOLOGIES_SQL)
        db.conn.execute(CREATE_UNITS_SQL)

        db.conn.executemany(INSERT_TECHNOLOGIES_SQL,
                            (d for d in data['technology']))
        db.conn.executemany(INSERT_UNITS_SQL,
                            (d for d in data['units']))
