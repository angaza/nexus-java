# Nexus Keycode: Java (Preview Release)

This package allows you to encode Nexus Keycodes.

This is a preview release. We anticipate producing a production-ready release
by the end of Q1 2020.

Learn more about about Angaza Nexus [here](https://angaza.github.io/nexus)!

## Installation

Source archives for each release are available in
[releases](https://github.com/angaza/nexus-keycode-java/releases).

This package comes with a full suite of unit tests, which you can run in the
normal way in your IDE.

## Versioning

This package uses a form of [semantic versioning](semver.org). The version
number is comprised of three components: MAJOR.MINOR.PATCH

Major version numbers represent breaking changes in the keycode protocol
itself. This is the only version number that is relevant to keycodes
themselves. For example, any keycode generated any version 1.X.Y of this
encoder will be valid on any version 1.X.Y of the [embedded decoder](https://github.com/angaza/nexus-keycode-embedded).

Minor version numbers represent breaking internal API changes. You may need
to modify your code to accomodate these changes.

Patch version numbers represent changes that are fully backward compatible.

## Usage

Generate keycodes for the full and small keypad protocols.

### Full Protocol ###

Add Credit

```java
byte[] secretKey = new HexToByteArray().convert("deadbeefdeadbeefdeadbeefdeadbeef");
int messageId = 42;
long seconds = 7 /* days */ * 24 /* hours */ * 60 /* minutes */ * 60 /* seconds */;
Date clampedTime = new Date();
KeycodeMetadata output = KeycodeFactory.addCredit(
        clampedTime,
        messageId,
        secretKey,
        KeycodeProtocol.FULL,
        seconds
);
String keycode = output.getKeycodeData().getKeycode();
// outputs *599 791 493 194 43#
```

Set Credit

```java
int messageId = 43;
int hours = 14 /* days */ * 24 /* hours */;
FullMessage message = FullMessage.setCredit(messageId, hours, secretKey);
String keycode = message.toKeycode();
// outputs *272 511 292 039 01#
```

Unlock

```java
int messageId = 44;
KeycodeMetadata output = KeycodeFactory.unlock(messageId, secretKey, KeycodeProtocol.FULL);
String keycode = output.getKeycodeData().getKeycode();
// outputs *578 396 697 305 45#
```

### Small Protocol ###

Add Credit

```java
int messageId = 31;
long seconds = 7 /* days */ * 24 /* hours */ * 60 /* minutes */ * 60 /* seconds */;
Date clampedTime = new Date();
KeycodeMetadata output = KeycodeFactory.addCredit(
        clampedTime,
        messageId,
        secretKey,
        KeycodeProtocol.SMALL,
        seconds
);
String keycode = output.getKeycodeData().getKeycode();
// outputs 154 535 324 353 534
```

Unlock

```java
int messageId = 32;
KeycodeMetadata output = KeycodeFactory.unlock(messageId, secretKey, KeycodeProtocol.SMALL);
String keycode = output.getKeycodeData().getKeycode();
// outputs 153 233 555 553 342
```
