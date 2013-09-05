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


def write_database(filepath, data):
    db = DatabaseAdapter(filepath)
    with db.conn:
        db.conn.execute(SQL_CREATE_METADATA)
        db.conn.execute(SQL_METADATA_INSERT)
        db.conn.execute(CREATE_TECHNOLOGIES_SQL)
        db.conn.executemany(INSERT_TECHNOLOGIES_SQL,
                            (d for d in data['technology']))
