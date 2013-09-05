"""
Creates the civilopedia_v.db Sqlite database file

This file is created from two Civ5 game databases Civ5DebugDatabase.db
and Localization-Merged.db. These files are typically stored in
~/My Documents/My Games/Civilization V/cache

This script is intended to run with Python2.7
"""

import os

import civ5


def create_database(db_dir, outdb='civilopedia.db'):
    data = {}

    # Initialize gamedata database
    game_db = civ5.GamedataDB(
        os.path.join(db_dir, civ5.DATABASES['Gamedata']))

    print('Fetching technologies')
    data['technology'] = list(game_db.get_technologies())

    print('Writing database')
    if os.path.isfile(outdb):
        print('Removing existing database first')
        os.remove(outdb)
    civ5.write_database(outdb, data)


if __name__ == '__main__':
    import argparse
    parser = argparse.ArgumentParser(description=
                                     'Create the civilopedia database.')
    parser.add_argument('--cachedir', dest='cachedir', required=True,
                        help='Path to the cache directory containing *.db')
    args = parser.parse_args()

    #  Perform a sanity check that the databases exist
    for fn in civ5.DATABASES.values():
        path = os.path.join(args.cachedir, fn)
        if not os.path.isfile(path):
            parser.error('{} not found in {}'.format(path, args.cachedir))

    create_database(args.cachedir)
