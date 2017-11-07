# PinMe

Location sharing app for android devices

## Design

Registered users can pin their current location and all the users who are following will get a notification of the pin. Followers can also request users to pin location at any time using request pin option. To follow a user go to follow tab and search them by user name and send follow request. If the user accepts the request you can see them in your following list.

## Error correction in GPS sensor

As we cannot rely on GPS sensor to be accurate every time, each subsequent pin on the same location does error correction. If future pins are not far away from the existing pin, all the existing pins are passed through low pass filter of factor 0.15. Please not that only average value of co ordinates are shown for a pin.
