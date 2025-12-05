# --------------- JJWT (JSON Web Token) ---------------
# Keep all JWT classes and members
-keep class io.jsonwebtoken.** { *; }
-keepnames class io.jsonwebtoken.* { *; }
-keepnames interface io.jsonwebtoken.* { *; }

# Keep BouncyCastle (often used for crypto)
-keep class org.bouncycastle.** { *; }
-keepnames class org.bouncycastle.** { *; }
-dontwarn org.bouncycastle.**