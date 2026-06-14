from http.server import HTTPServer, BaseHTTPRequestHandler

HTML = """<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>SimpMusic</title>
<style>
  * { box-sizing: border-box; margin: 0; padding: 0; }
  body { font-family: 'Segoe UI', sans-serif; background: #0f0f0f; color: #e0e0e0; min-height: 100vh; display: flex; flex-direction: column; align-items: center; padding: 40px 20px; }
  .hero { text-align: center; margin-bottom: 48px; }
  .logo { width: 96px; height: 96px; background: linear-gradient(135deg, #6200EE, #03DAC6); border-radius: 24px; display: flex; align-items: center; justify-content: center; margin: 0 auto 24px; font-size: 48px; }
  h1 { font-size: 2.5rem; font-weight: 700; background: linear-gradient(135deg, #BB86FC, #03DAC6); -webkit-background-clip: text; -webkit-text-fill-color: transparent; margin-bottom: 12px; }
  .subtitle { color: #aaa; font-size: 1.1rem; max-width: 600px; line-height: 1.6; }
  .badge { display: inline-block; background: #1e1e2e; border: 1px solid #333; border-radius: 20px; padding: 4px 14px; font-size: 0.8rem; color: #BB86FC; margin: 4px; }
  .card-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(280px, 1fr)); gap: 20px; max-width: 900px; width: 100%; }
  .card { background: #1a1a2e; border: 1px solid #2a2a3e; border-radius: 16px; padding: 24px; }
  .card h2 { color: #BB86FC; margin-bottom: 16px; font-size: 1.1rem; }
  .card ul { list-style: none; }
  .card li { padding: 6px 0; color: #ccc; border-bottom: 1px solid #2a2a3e; font-size: 0.9rem; }
  .card li:last-child { border-bottom: none; }
  .card li::before { content: "▸ "; color: #03DAC6; }
  .notice { background: #1e1a2e; border: 1px solid #BB86FC44; border-radius: 12px; padding: 20px 28px; max-width: 900px; width: 100%; margin-top: 20px; }
  .notice h3 { color: #BB86FC; margin-bottom: 8px; }
  .notice p { color: #aaa; line-height: 1.6; font-size: 0.95rem; }
  code { background: #2a2a3e; padding: 2px 8px; border-radius: 4px; font-family: monospace; color: #03DAC6; font-size: 0.9em; }
  .badges { margin: 16px 0; }
</style>
</head>
<body>
  <div class="hero">
    <div class="logo">🎵</div>
    <h1>SimpMusic</h1>
    <p class="subtitle">A FOSS YouTube Music client for Android &amp; Desktop with Spotify features, SponsorBlock, Return YouTube Dislike, and AI lyrics translation — built with Compose Multiplatform (KMP).</p>
    <div class="badges">
      <span class="badge">Kotlin</span>
      <span class="badge">Compose Multiplatform</span>
      <span class="badge">Android</span>
      <span class="badge">Desktop (JVM)</span>
      <span class="badge">Gradle 9.5</span>
      <span class="badge">MVVM + Clean Architecture</span>
    </div>
  </div>

  <div class="card-grid">
    <div class="card">
      <h2>✨ Features</h2>
      <ul>
        <li>YouTube Music streaming (no ads)</li>
        <li>High quality up to 256kbps</li>
        <li>SponsorBlock &amp; Return YouTube Dislike</li>
        <li>Synced lyrics (LRCLIB, Spotify, AI)</li>
        <li>Spotify Canvas support</li>
        <li>Discord Rich Presence</li>
        <li>Android Auto support</li>
        <li>Crossfade &amp; Sleep Timer</li>
      </ul>
    </div>
    <div class="card">
      <h2>📦 Modules</h2>
      <ul>
        <li>androidApp — Android entry point</li>
        <li>desktopApp — Desktop entry point</li>
        <li>composeApp — Shared UI &amp; ViewModels</li>
        <li>core:domain — Business logic</li>
        <li>core:data — Repositories &amp; DB</li>
        <li>core:service — YTMusic, Spotify, AI</li>
        <li>core:media-* — Media player</li>
      </ul>
    </div>
    <div class="card">
      <h2>🛠 Build Commands</h2>
      <ul>
        <li><code>./gradlew :androidApp:assembleDebug</code></li>
        <li><code>./gradlew :composeApp:vlcSetup</code></li>
        <li><code>./gradlew :desktopApp:run</code></li>
        <li><code>./build_and_sign_apk.sh</code></li>
      </ul>
    </div>
  </div>

  <div class="notice">
    <h3>ℹ️ About this Replit environment</h3>
    <p>SimpMusic is a native <strong>Android &amp; Desktop</strong> application — it runs on Android devices and Windows/macOS/Linux desktops, not in a web browser. This page is an informational overview. To build the app, use the Gradle commands above. You'll need a JDK and Android SDK for Android builds, or just a JDK for the desktop build.</p>
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

    def log_message(self, format, *args):
        pass

if __name__ == "__main__":
    server = HTTPServer(("0.0.0.0", 5000), Handler)
    print("SimpMusic info page running on http://0.0.0.0:5000")
    server.serve_forever()
