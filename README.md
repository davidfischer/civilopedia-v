Civilopedia V
=============

This application contains an offline version of the
[Civilization V][civilization-v] civilopedia.

This app is available in [Google Play][civilopedia-google-play].
No Android permissions whatsoever are required to use this app.


Building
--------

To build the app, you will need to either set the `$ANDROID_HOME` environment
variable or create a `local.properties` file specifying the location of the
Android SDK (`sdk.dir`).

You will also need [Apache Ant][apache-ant], a command line build
tool for Java.

Once these are installed, the application can be built:

    % ant clean debug


License
-------

See the associated LICENSE.md and NOTICE.md files for details.


[civilization-v]: http://www.civilization5.com
[civilopedia-google-play]: https://play.google.com/store/apps/details?id=name.davidfischer.civilopedia
[apache-ant]: http://ant.apache.org
