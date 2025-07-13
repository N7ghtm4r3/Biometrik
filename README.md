# Biometrik

Biometric authentication for Compose Multiplatform applications

# TODO:

## Android

To document about the implementation about app-compact and related conversion of the MainActivity and the theme

## Wasm

To document for wasmJs to add the compiler flag to handle exceptions

```kotlin
compilerOptions {
    freeCompilerArgs.add("-Xwasm-attach-js-exception")
}
```

## Jvm

### Windows

- To docu about to use the Progetto/Proprietà/C-C++/Linguaggio/Standard-LinguaggioC++ set -> Standard C++17 ISO (/std:
  c++17)

- To docu about to add the Progetto/Proprietà/Linker/Input/DipendenzeAggiuntive add -> windowsapp.lib

### Linux

To docu about how to compile the linuxnative engine and that for the moment is used without bioauth

```bash
gcc -fPIC -shared -o LinuxPolkitEngine.so  PolkitEngine.c $(pkg-config --cflags --libs polkit-gobject-1 gio-2.0 glib-2.0)
```
