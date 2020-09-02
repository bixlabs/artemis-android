#Artemis Pro

In order to build and run this project ensure to read the following sections

## Release and signature
In order to be able to create signed bundle releases you will need two files the [artemis.jks][1]
keystore and the [keystore.properties][2], these files should be placed on the project root

Ensure that the property `storeFile` defined in the [keystore.properties][2] point to the absolute
path of the file [artemis.jks][1] on your system.

## Crash reporting
Crash reporting is enabled on the application via Firebase Crashlytics
in order to be able to build the project you will need a [google-services.json][3] file, you need
to place it inside the app folder in the project root.

[1]: https://drive.google.com/file/d/180vab4G9P_8VwQoHe5YoTrYMvdrA4wIs/view?usp=sharing
[2]: https://drive.google.com/file/d/1bRqBYulRYSXuS-Q6nFSfQBEcWYgi3fvj/view?usp=sharing
[3]: https://drive.google.com/file/d/16cCv70IWOnJoHHL0jwmlN29sklzovpeh/view?usp=sharing
