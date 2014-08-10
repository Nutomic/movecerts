## Move Certs!

"Network may be monitored": Tired of this warning after installing a certificate?

With this app, you can disable it in one click.

**REQUIRES ROOT**

[![Get it on Google Play](https://developer.android.com/images/brand/en_generic_rgb_wo_60.png)](https://play.google.com/store/apps/details?id=com.nutomic.zertman) [![Get it on F-Droid](https://f-droid.org/wiki/images/0/06/F-Droid-button_get-it-on.png)](https://f-droid.org/repository/browse/?fdfilter=move%20certs&fdid=com.nutomic.zertman)

Thanks to:
http://forum.xda-developers.com/google-nexus-5/help/howto-install-custom-cert-network-t2533550
https://stackoverflow.com/questions/13981011/cacerts-bks-does-not-exist/18390177#18390177

## Building

Clone the project with `git clone --recursive`

Build with `gradle assembleDebug` or `gradle assembleRelease`. Alternatively, import the project into Android Studio.

## Dependencies

The [libsuperuser](https://github.com/Chainfire/libsuperuser) library (licensed under Apache 2.0)  is used for root access, and included as a git submodule in the `libraries/` folder.

## License

The project is licensed under [GPL v3](LICENSE.md).
