{pkgs}: {
  deps = [
    pkgs.lttng-ust
    pkgs.libkrb5
    pkgs.zlib
    pkgs.openssl
    pkgs.icu
    pkgs.wget
    pkgs.curl
    pkgs.git
    pkgs.jdk17
  ];
}
