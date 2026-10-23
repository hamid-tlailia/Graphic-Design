#!/usr/bin/env python3
"""Builds the vertical promo reel (1080x1920), its cover image and captions.

Scenes are laid out in HTML, rendered with headless Chromium, then animated
and joined with ffmpeg. App screenshots come from the Robolectric screenshot
tests:  ./gradlew testDebugUnitTest --tests '*ScreenshotTest*'

Usage:  python3 promo/make_reel.py --phone "+216 12 345 678"
"""
import argparse, base64, io, os, re, shutil, subprocess, urllib.parse
from pathlib import Path

import qrcode

ROOT = Path(__file__).resolve().parent.parent
SHOTS = ROOT / "app/build/screens"
FONTS = ROOT / "app/src/main/res/font"
OUT = ROOT / "promo/out"
WORK = OUT / ".work"
CHROME = "/opt/pw-browsers/chromium_headless_shell-1194/chrome-linux/headless_shell"
W, H, FPS = 1080, 1920, 30

NAVY, CORAL, LILAC, CREAM, INK = "#2A2470", "#F2603F", "#C9C5F5", "#F7F6F2", "#1D1A3F"

# (screenshot(s), Arabic title, English line, chip, seconds)
SCENES = [
    (["29_path"], "تعلّم التصميم الجرافيكي<br>خطوة بخطوة", "Learn graphic design step by step", "34 درساً · 9 وحدات", 2.8),
    (["03_quiz"], "تمارين تفاعلية<br>وتحدٍّ يومي", "Interactive drills & a daily challenge", "نقاط · سلسلة أيام · أوسمة", 2.6),
    (["17_canvas"], "استوديو تصميم حر<br>بالسحب والإفلات", "A free drag-and-drop design studio", "صور · أيقونات · خطوط عربية", 2.6),
    (["11_identity"], "اصنع شعارك<br>وهويتك البصرية", "Create your logo & visual identity", "+2000 أيقونة · 19 خطاً", 2.6),
    (["13_build"], "ابنِ واجهات تطبيقك<br>بالبلوكات الجاهزة", "Build app screens from ready blocks", "هيدر · بطاقات · قوائم · فوتر", 2.8),
    (["15_export"], "صدّر عملك<br>بضغطة واحدة", "Export in one tap", "PNG · PDF · HTML · React Native", 2.6),
    (["21_home_en", "20_home_dark"], "عربي و English<br>فاتح وداكن", "Arabic & English · light & dark", "تلقائي أو يدوي", 2.8),
]
INTRO_SECONDS, CTA_SECONDS, XFADE = 2.6, 4.6, 0.45


def font_face():
    faces = [("Tajawal", "tajawal_regular.ttf", 400), ("Tajawal", "tajawal_bold.ttf", 800),
             ("Plex", "plex_regular.ttf", 400), ("Plex", "plex_semibold.ttf", 600), ("Plex", "plex_bold.ttf", 700)]
    return "\n".join(f"@font-face{{font-family:'{f}';src:url('file://{FONTS / n}');font-weight:{w}}}" for f, n, w in faces)


def data_uri(path: Path) -> str:
    return "data:image/png;base64," + base64.b64encode(path.read_bytes()).decode()


EYE = f"""<svg viewBox="0 0 120 120" class="eye"><rect width="120" height="120" rx="34" fill="#fff"/>
<path d="M18 60 Q60 22 102 60 Q60 98 18 60 Z" fill="none" stroke="{NAVY}" stroke-width="7" stroke-linejoin="round"/>
<circle cx="60" cy="60" r="17" fill="{CORAL}"/><circle cx="66" cy="54" r="4.5" fill="#fff"/></svg>"""

BASE_CSS = f"""
{font_face()}
*{{box-sizing:border-box;margin:0}}
html,body{{width:{W}px;height:{H}px;overflow:hidden}}
body{{font-family:'Tajawal','Plex',sans-serif;color:#fff;background:transparent}}
.stage{{position:fixed;left:0;top:0;width:{W}px;height:{H}px;overflow:hidden}}
.bg{{position:absolute;inset:0;background:{NAVY};overflow:hidden}}
.bg:before{{content:'';position:absolute;inset:0;background-image:radial-gradient(rgba(255,255,255,.10) 2px,transparent 2.5px);background-size:36px 36px}}
.blob{{position:absolute;border-radius:50%;filter:blur(90px);opacity:.55}}
.en{{font-family:'Plex';font-weight:600}}
.phone{{position:absolute;border-radius:78px;background:#0E0C24;padding:16px;box-shadow:0 40px 90px rgba(0,0,0,.45),0 0 0 2px rgba(255,255,255,.08) inset}}
.phone img{{display:block;width:100%;border-radius:62px}}
.eye{{width:100%;height:100%;filter:drop-shadow(0 24px 50px rgba(0,0,0,.35))}}
"""


def page(body: str, transparent=False) -> str:
    bg = "" if transparent else "<div class='bg'><div class='blob' style='width:700px;height:700px;background:#6C5CE7;top:-200px;right:-260px'></div><div class='blob' style='width:520px;height:520px;background:" + CORAL + ";bottom:-160px;left:-200px;opacity:.35'></div></div>"
    return f"<!doctype html><html dir='rtl'><head><meta charset='utf-8'><style>{BASE_CSS}</style></head><body><div class='stage'>{bg}{body}</div></body></html>"


def render(name: str, html: str, transparent=False) -> Path:
    src = WORK / f"{name}.html"; png = WORK / f"{name}.png"
    src.write_text(html, encoding="utf-8")
    args = [CHROME, "--no-sandbox", "--disable-gpu", "--hide-scrollbars", "--force-device-scale-factor=1",
            f"--window-size={W},{H}", "--virtual-time-budget=3000", f"--screenshot={png}", src.as_uri()]
    if transparent:
        args.insert(1, "--default-background-color=00000000")
    subprocess.run(args, check=True, stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
    return png


def phones(shots, width=600, top=640):
    """One centred phone, or two overlapping ones."""
    if len(shots) == 1:
        return f"<div class='phone' style='width:{width}px;left:{(W - width) // 2}px;top:{top}px'><img src='{data_uri(SHOTS / (shots[0] + '.png'))}'></div>"
    w = 500
    a, b = shots
    return (f"<div class='phone' style='width:{w}px;left:70px;top:{top + 70}px;transform:rotate(-5deg)'><img src='{data_uri(SHOTS / (a + '.png'))}'></div>"
            f"<div class='phone' style='width:{w}px;right:70px;top:{top}px;transform:rotate(5deg)'><img src='{data_uri(SHOTS / (b + '.png'))}'></div>")


def caption(i, title, en, chip):
    return f"""<div style="position:absolute;top:150px;left:80px;right:80px;text-align:center">
  <div class="en" style="display:inline-block;font-size:30px;letter-spacing:4px;color:{CORAL};background:rgba(242,96,63,.14);padding:10px 26px;border-radius:40px">0{i}</div>
  <h1 style="font-size:78px;line-height:1.18;font-weight:800;margin-top:26px">{title}</h1>
  <p class="en" dir="ltr" style="font-size:34px;color:{LILAC};margin-top:18px">{en}</p>
  <div style="display:inline-block;margin-top:26px;font-size:32px;font-weight:800;color:{NAVY};background:#fff;padding:12px 30px;border-radius:40px">{chip}</div>
</div>"""


def intro_html(shot):
    return page(f"""
<div style="position:absolute;top:170px;left:0;right:0;text-align:center">
  <div style="width:210px;height:210px;margin:0 auto">{EYE}</div>
  <h1 style="font-size:150px;font-weight:800;line-height:1.1;margin-top:34px">عين</h1>
  <p class="en" dir="ltr" style="font-size:44px;letter-spacing:6px;color:{LILAC}">EYN · DESIGN TRAINER</p>
  <p style="font-size:46px;margin-top:30px;color:#fff">درّب عين المصمم فيك</p>
  <p class="en" dir="ltr" style="font-size:32px;margin-top:8px;color:{LILAC}">Train your designer's eye</p>
</div>
{phones([shot], 560, 1040)}""")


def cover_html():
    return page(f"""
<div style="position:absolute;top:150px;left:0;right:0;text-align:center">
  <div style="width:170px;height:170px;margin:0 auto">{EYE}</div>
  <h1 style="font-size:120px;font-weight:800;line-height:1.1;margin-top:26px">عين</h1>
  <p style="font-size:60px;font-weight:800;margin-top:14px;line-height:1.25">تعلّم التصميم الجرافيكي<br><span style="color:{CORAL}">وابنِ تطبيقك</span></p>
  <p class="en" dir="ltr" style="font-size:36px;margin-top:22px;color:{LILAC}">Learn graphic design &amp; build your app</p>
</div>
<div class="phone" style="width:420px;left:40px;top:980px;transform:rotate(-8deg)"><img src="{data_uri(SHOTS / '29_path.png')}"></div>
<div class="phone" style="width:420px;right:40px;top:980px;transform:rotate(8deg)"><img src="{data_uri(SHOTS / '13_build.png')}"></div>
<div class="phone" style="width:470px;left:305px;top:900px;z-index:2"><img src="{data_uri(SHOTS / '02_home.png')}"></div>""")


def wa_link(phone: str) -> str:
    digits = re.sub(r"\D", "", phone)
    text = urllib.parse.quote("مرحباً، أريد معرفة المزيد عن تطبيق عين لتعلّم التصميم")
    return f"https://wa.me/{digits}?text={text}"


def qr_uri(link: str) -> str:
    q = qrcode.QRCode(error_correction=qrcode.constants.ERROR_CORRECT_M, box_size=16, border=2)
    q.add_data(link); q.make(fit=True)
    buf = io.BytesIO(); q.make_image(fill_color=INK, back_color="white").save(buf, format="PNG")
    return "data:image/png;base64," + base64.b64encode(buf.getvalue()).decode()


WA_ICON = """<svg viewBox="0 0 32 32" width="64" height="64"><path fill="#25D366" d="M16 2.7A13.2 13.2 0 0 0 4.6 22.6L2.7 29.3l6.9-1.8A13.2 13.2 0 1 0 16 2.7z"/>
<path fill="#fff" d="M23.2 19.3c-.4-.2-2.3-1.1-2.6-1.3-.4-.1-.6-.2-.9.2l-1.2 1.5c-.2.3-.4.3-.8.1a10.8 10.8 0 0 1-5.4-4.7c-.4-.7.4-.6 1.1-2 .1-.2.1-.5 0-.7l-1.2-2.8c-.3-.8-.6-.6-.9-.7h-.7c-.3 0-.7.1-1 .5-.4.4-1.4 1.3-1.4 3.2s1.4 3.7 1.6 4c.2.3 2.7 4.2 6.6 5.8 2.5 1 3.4 1.1 4.7.9.8-.1 2.3-.9 2.6-1.8.3-.9.3-1.7.2-1.8-.1-.2-.4-.3-.7-.4z"/></svg>"""


def cta_html(phone: str):
    return page(f"""
<div style="position:absolute;top:120px;left:0;right:0;text-align:center">
  <div style="width:120px;height:120px;margin:0 auto">{EYE}</div>
  <h1 style="font-size:72px;font-weight:800;margin-top:24px;line-height:1.2">ابدأ رحلتك<br>في التصميم اليوم</h1>
  <p class="en" dir="ltr" style="font-size:36px;color:{LILAC};margin-top:16px">Start your design journey today</p>
</div>
<div style="position:absolute;top:600px;left:220px;right:220px;background:#fff;border-radius:52px;padding:40px 40px 34px;text-align:center;box-shadow:0 40px 90px rgba(0,0,0,.4)">
  <img src="{qr_uri(wa_link(phone))}" style="width:100%;display:block;border-radius:18px">
  <p style="color:{INK};font-size:34px;font-weight:800;margin-top:18px">امسح الكود وراسلنا على واتساب</p>
  <p class="en" dir="ltr" style="color:#7C7A92;font-size:28px;margin-top:6px">Scan to chat with us on WhatsApp</p>
</div>
<div dir="ltr" style="position:absolute;top:1360px;left:0;right:0;display:flex;justify-content:center;align-items:center;gap:22px">
  {WA_ICON}<span class="en" style="font-size:66px;font-weight:700;letter-spacing:2px">{phone}</span>
</div>""")


def ff(*args):
    subprocess.run(["ffmpeg", "-y", "-loglevel", "error", *args], check=True)


def clip(name, bg: Path, fg: Path | None, seconds: float, slide=True):
    """A scene: still background with a gentle zoom; the phone layer eases up from below."""
    out = WORK / f"{name}.mp4"
    zoom = f"scale=w='trunc({W}*(1+0.035*t/{seconds})/2)*2':h=-2:eval=frame,crop={W}:{H}"
    if fg is None:
        ff("-loop", "1", "-t", str(seconds), "-i", str(bg), "-vf", f"{zoom},fps={FPS},format=yuv420p",
           "-c:v", "libx264", "-preset", "medium", "-crf", "18", str(out))
        return out
    y = f"280*pow(1-min(t/0.75\\,1)\\,3)+6*sin(t*2.2)" if slide else "6*sin(t*2.2)"
    ff("-loop", "1", "-t", str(seconds), "-i", str(bg), "-loop", "1", "-t", str(seconds), "-i", str(fg),
       "-filter_complex", f"[0]{zoom}[b];[1]format=rgba[p];[b][p]overlay=x=0:y='{y}':eval=frame,fps={FPS},format=yuv420p",
       "-c:v", "libx264", "-preset", "medium", "-crf", "18", str(out))
    return out


def join(clips, durations, out: Path):
    inputs = sum([["-i", str(c)] for c in clips], [])
    parts, last, offset = [], "[0]", 0.0
    transitions = ["slideleft", "smoothup", "slideleft", "circleopen", "slideleft", "smoothup", "slideleft", "fade"]
    for i in range(1, len(clips)):
        offset += durations[i - 1] - XFADE
        tag = f"[v{i}]"
        parts.append(f"{last}[{i}]xfade=transition={transitions[(i - 1) % len(transitions)]}:duration={XFADE}:offset={offset:.3f}{tag}")
        last = tag
    total = sum(durations) - XFADE * (len(clips) - 1)
    # Silent stereo track: some platforms reject video-only uploads; add music in the app.
    ff(*inputs, "-f", "lavfi", "-t", f"{total:.3f}", "-i", "anullsrc=r=44100:cl=stereo",
       "-filter_complex", ";".join(parts), "-map", last, "-map", f"{len(clips)}:a",
       "-c:v", "libx264", "-preset", "slow", "-crf", "19", "-profile:v", "high", "-pix_fmt", "yuv420p",
       "-c:a", "aac", "-b:a", "128k", "-shortest", "-movflags", "+faststart", str(out))
    return total


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--phone", default="+000 00 000 000", help="WhatsApp number shown on screen and in the QR code")
    a = ap.parse_args()
    shutil.rmtree(WORK, ignore_errors=True); WORK.mkdir(parents=True)

    cover = render("cover", cover_html())
    ff("-i", str(cover), "-q:v", "2", str(OUT / "cover.jpg"))

    clips, durs = [clip("intro", render("intro", intro_html("02_home")), None, INTRO_SECONDS)], [INTRO_SECONDS]
    for i, (shots, title, en, chip, sec) in enumerate(SCENES, 1):
        bg = render(f"s{i}_bg", page(caption(i, title, en, chip)))
        fg = render(f"s{i}_fg", page(phones(shots), transparent=True), transparent=True)
        clips.append(clip(f"s{i}", bg, fg, sec)); durs.append(sec)
    clips.append(clip("cta", render("cta", cta_html(a.phone)), None, CTA_SECONDS)); durs.append(CTA_SECONDS)

    total = join(clips, durs, OUT / "eyn_reel.mp4")
    shutil.rmtree(WORK, ignore_errors=True)
    print(f"reel: {OUT / 'eyn_reel.mp4'} ({total:.1f}s)\ncover: {OUT / 'cover.jpg'}\nQR → {wa_link(a.phone)}")


if __name__ == "__main__":
    main()
