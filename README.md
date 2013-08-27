Civilopedia V
=============

This application contains an offline version of the
[Civilization V](http://www.civilization5.com/) civilopedia.

All artwork is extracted from the Civilization V game itself and is
owned by the appropriate rights holder.

No Android permissions whatsoever are required to use this app.


Building
--------

To build the app, you will need to either set the `$ANDROID_HOME` environment
variable or create a `local.properties` file specifying the location of the
Android SDK (`sdk.dir`).

You will also need [Apache Ant](http://ant.apache.org/), a command line build
tool for Java.

Once these are installed, the application can be built:

    % ant clean debug
