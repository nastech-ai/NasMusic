from http.server import HTTPServer, BaseHTTPRequestHandler

HTML = """<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>NasMusic — NasTech</title>
<style>
  *{box-sizing:border-box;margin:0;padding:0}
  body{font-family:'Segoe UI',sans-serif;background:#0a0a14;color:#e0e0e0;min-height:100vh;padding:40px 20px}
  .hero{text-align:center;margin-bottom:48px}
  .logo{width:96px;height:96px;background:linear-gradient(135deg,#6200EE,#03DAC6);border-radius:24px;display:flex;align-items:center;justify-content:center;margin:0 auto 20px;font-size:48px}
  h1{font-size:2.4rem;font-weight:700;background:linear-gradient(135deg,#BB86FC,#03DAC6);-webkit-background-clip:text;-webkit-text-fill-color:transparent;margin-bottom:8px}
  .sub{color:#aaa;font-size:1rem;margin-bottom:16px}
  .badge{display:inline-block;background:#1e1e2e;border:1px solid #333;border-radius:20px;padding:3px 12px;font-size:0.78rem;color:#BB86FC;margin:3px}
  h2{font-size:1rem;color:#BB86FC;margin-bottom:14px;display:flex;align-items:center;gap:8px}
  .grid{display:grid;grid-template-columns:repeat(auto-fit,minmax(300px,1fr));gap:16px;max-width:1100px;margin:0 auto 24px}
  .card{background:#111122;border:1px solid #222240;border-radius:14px;padding:20px}
  .card ul{list-style:none}
  .card li{padding:5px 0;color:#ccc;border-bottom:1px solid #1a1a30;font-size:0.85rem;display:flex;justify-content:space-between;align-items:center;gap:8px}
  .card li:last-child{border:none}
  .tag{font-size:0.7rem;padding:2px 8px;border-radius:10px;white-space:nowrap}
  .done{background:#1a3a2a;color:#4caf50;border:1px solid #2d5c3e}
  .need{background:#3a2a1a;color:#ff9800;border:1px solid #5c3e1a}
  .info{background:#1a2a3a;color:#2196f3;border:1px solid #1a3a5c}
  code{background:#1e1e2e;padding:2px 8px;border-radius:4px;font-family:monospace;color:#03DAC6;font-size:0.82em}
  .section{max-width:1100px;margin:0 auto 20px}
  .url-table{width:100%;border-collapse:collapse;font-size:0.85rem}
  .url-table th{background:#1a1a2e;color:#BB86FC;padding:8px 12px;text-align:left;border-bottom:1px solid #2a2a4e}
  .url-table td{padding:7px 12px;border-bottom:1px solid #1a1a2e;color:#ccc}
  .url-table tr:hover td{background:#111128}
  .url-table code{font-size:0.8em}
  .arrow{color:#555;margin:0 4px}
  strike{color:#555}
</style>
</head>
<body>
<div class="hero">
  <div class="logo">🎵</div>
  <h1>NasMusic</h1>
  <p class="sub">by NasTech · nastechai.com · github.com/nastech-ai/NasMusic</p>
  <div>
    <span class="badge">Kotlin / KMP</span>
    <span class="badge">Compose Multiplatform</span>
    <span class="badge">Android + Desktop</span>
    <span class="badge">Gradle 9.5</span>
    <span class="badge">com.nastechai.nasmusic</span>
  </div>
</div>

<div class="grid">

  <div class="card">
    <h2>🏗️ Repo Structure (All-in-One)</h2>
    <ul>
      <li><code>androidApp/</code> <span class="tag done">✓ branded</span></li>
      <li><code>composeApp/</code> <span class="tag done">✓ branded</span></li>
      <li><code>desktopApp/</code> <span class="tag done">✓ branded</span></li>
      <li><code>core/common/</code> <span class="tag done">✓ embedded</span></li>
      <li><code>core/data/</code> <span class="tag done">✓ embedded</span></li>
      <li><code>core/domain/</code> <span class="tag done">✓ embedded</span></li>
      <li><code>core/service/</code> <span class="tag need">⚠ add manually</span></li>
      <li><code>core/media/</code> <span class="tag need">⚠ add manually</span></li>
      <li><code>libs/gemini-kotlin/</code> <span class="tag done">✓ embedded</span></li>
      <li><code>libs/NowPlayingCenter/</code> <span class="tag done">✓ embedded</span></li>
      <li><code>libs/BravePipeExtractor/</code> <span class="tag done">✓ embedded</span></li>
      <li><code>libs/MediaServiceCore/</code> <span class="tag done">✓ embedded</span></li>
      <li><code>libs/SharedModules/</code> <span class="tag done">✓ embedded</span></li>
      <li><code>libs/mediasession-kt/</code> <span class="tag done">✓ embedded</span></li>
      <li><code>crashlytics/</code> <span class="tag done">✓ branded</span></li>
    </ul>
  </div>

  <div class="card">
    <h2>🏷️ Brand Changes Applied</h2>
    <ul>
      <li>App name <span><strike>SimpMusic</strike> → <strong>NasMusic</strong></span></li>
      <li>Developer <span><strike>maxrave</strike> → <strong>nastechai</strong></span></li>
      <li>Company <span><strike>Maxrave</strike> → <strong>NasTech</strong></span></li>
      <li>Android ID <span><code>com.nastechai.nasmusic</code></span></li>
      <li>URL scheme <span><code>nasmusic://</code></span></li>
      <li>Website <span><code>nastechai.com</code></span></li>
      <li>Chart <span><code>chart.nastechai.com</code></span></li>
      <li>Deep links <span><code>nastechai.com/app/...</code></span></li>
      <li>GitHub org <span><code>nastech-ai</code></span></li>
      <li>Maven group <span><code>org.nastechai</code></span></li>
      <li>Package root <span><code>com.nastechai.*</code></span></li>
      <li>1138 files renamed <span class="tag done">✓ done</span></li>
    </ul>
  </div>

  <div class="card">
    <h2>📦 NasTech Libraries to Publish</h2>
    <ul>
      <li><code>org.nastechai.gemini-kotlin:openai-client</code> <span class="tag need">publish</span></li>
      <li><code>org.nastechai:nowplayingcenter</code> <span class="tag need">publish</span></li>
      <li><code>org.nastechai:jmtc</code> <span class="tag need">publish</span></li>
      <li>Maven repo → <code>nastech-ai/maven-repo</code> <span class="tag need">create</span></li>
      <li>Run <code>./gradlew :nowplayingcenter:publish</code></li>
      <li>Run <code>./gradlew :jmtc:publish</code></li>
      <li>Run <code>./gradlew :openai-client:publish</code></li>
    </ul>
  </div>

  <div class="card">
    <h2>🔗 GitHub Repos to Create (nastech-ai org)</h2>
    <ul>
      <li>nastech-ai/NasMusic <span class="tag need">main app</span></li>
      <li>nastech-ai/core <span class="tag need">common/data/domain</span></li>
      <li>nastech-ai/gemini-kotlin <span class="tag need">AI client lib</span></li>
      <li>nastech-ai/NowPlayingCenter <span class="tag need">jmtc + nowplaying</span></li>
      <li>nastech-ai/BravePipeExtractor <span class="tag need">YT scraper</span></li>
      <li>nastech-ai/MediaServiceCore <span class="tag need">media interfaces</span></li>
      <li>nastech-ai/SharedModules <span class="tag need">shared utils</span></li>
      <li>nastech-ai/mediasession-kt <span class="tag need">media session</span></li>
      <li>nastech-ai/maven-repo <span class="tag need">lib artifacts</span></li>
    </ul>
  </div>

  <div class="card">
    <h2>⚠️ Missing — Need to Add</h2>
    <ul>
      <li><code>core/service/ktorExt</code> <span class="tag need">not public</span></li>
      <li><code>core/service/aiService</code> <span class="tag need">not public</span></li>
      <li><code>core/service/lyricsService</code> <span class="tag need">not public</span></li>
      <li><code>core/service/kotlinYtmusicScraper</code> <span class="tag need">not public</span></li>
      <li><code>core/service/spotify</code> <span class="tag need">not public</span></li>
      <li><code>core/service/kizzy</code> <span class="tag need">not public</span></li>
      <li><code>core/media/media-jvm</code> <span class="tag need">not public</span></li>
      <li><code>core/media/media-jvm-ui</code> <span class="tag need">not public</span></li>
      <li><code>core/media/media3</code> <span class="tag need">not public</span></li>
      <li><code>core/media/media3-ui</code> <span class="tag need">not public</span></li>
    </ul>
  </div>

  <div class="card">
    <h2>🌐 URL Map</h2>
    <ul>
      <li>Main site <span><code>nastechai.com</code></span></li>
      <li>App portal <span><code>nastechai.com/app/...</code></span></li>
      <li>Charts <span><code>chart.nastechai.com</code></span></li>
      <li>Download <span><code>nastechai.com/download</code></span></li>
      <li>Nightly <span><code>nastechai.com/nightly-download</code></span></li>
      <li>Blog <span><code>nastechai.com/blogs/...</code></span></li>
      <li>Watch link <span><code>nastechai.com/app/watch?v=</code></span></li>
      <li>Playlist link <span><code>nastechai.com/app/playlist?list=</code></span></li>
      <li>Channel link <span><code>nastechai.com/app/channel/</code></span></li>
    </ul>
  </div>

</div>

<div class="section">
  <div class="card">
    <h2>🛠️ Build Commands</h2>
    <ul>
      <li><code>./gradlew :androidApp:assembleDebug</code> <span class="tag info">Android APK</span></li>
      <li><code>./gradlew :composeApp:vlcSetup</code> <span class="tag info">Desktop VLC natives</span></li>
      <li><code>./gradlew :desktopApp:run</code> <span class="tag info">Run desktop app</span></li>
      <li><code>./build_and_sign_apk.sh</code> <span class="tag info">Signed release APK</span></li>
    </ul>
  </div>
</div>

</body>
</html>
"""

class Handler(BaseHTTPRequestHandler):
    def do_GET(self):
        self.send_response(200)
        self.send_header("Content-Type", "text/html; charset=utf-8")
        self.end_headers()
        self.wfile.write(HTML.encode())
    def log_message(self, format, *args): pass

if __name__ == "__main__":
    server = HTTPServer(("0.0.0.0", 5000), Handler)
    print("NasMusic info page running on http://0.0.0.0:5000")
    server.serve_forever()
