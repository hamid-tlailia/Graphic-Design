# Promo reel

`out/eyn_reel.mp4` is a 1080×1920 reel of about 22 seconds. `out/cover.jpg` is its thumbnail and `out/caption.md` holds the bilingual description and hashtags.

To rebuild it with your WhatsApp number (the QR code opens a chat with that number):

```bash
./gradlew testDebugUnitTest --tests '*ScreenshotTest*'   # refresh app screenshots
pip install qrcode
python3 promo/make_reel.py --phone "+974 7100 9494"
```

It needs ffmpeg and the Playwright Chromium at `/opt/pw-browsers`; set `CHROME` in the script to use another path.

For TikTok, which removes videos that show a phone number or a QR code pointing outside the app, build the version that ends on a follow card instead:

```bash
python3 promo/make_reel.py --tiktok      # → out/eyn_reel_tiktok.mp4, caption in out/caption_tiktok.md
```
