# JVM fine-tuning

If you need to fine-tune native engines, follow the steps below depending on which engine you need to modify

## Windows

If you need to change the Windows's engine you can find the [Visual Studio](https://visualstudio.microsoft.com)
documented
files project where you can apply your modification:

``` bash
nativeengines
├── windows
    └── ... files ...
```

The requirements are:

- The minimum required version is `Standard C++ 17 ISO (/std:c++17)`
- Include the required `windowsapp.lib` library in the additional linker input dependencies

## Linux

Currently, authentication on Linux is supported via **Polkit**. Biometric support may be considered in future releases.

To modify the native engine you can find its file on:

``` bash
nativeengines
├── linux
    └── PolkitEngine.c
```

After applied the modification you needed, you can compile it with the below command:

```bash
gcc -fPIC -shared -o LinuxPolkitEngine.so  PolkitEngine.c $(pkg-config --cflags --libs polkit-gobject-1 gio-2.0 glib-2.0)
```

## MacOs

To modify the native engine you can find its file on:

``` bash
nativeengines
├── macos
    └── LocalAuthenticationEngine.m
```

After applied the modification you needed, you can compile it with the below command:

```bash
clang -framework Foundation -framework LocalAuthentication -shared -o LocalAuthenticationEngine.dylib LocalAuthenticationEngine.m
```

## Place the dynamic libraries

Once compiled, place the dynamic libraries in the appropriate platform-specific directories

``` bash
resources
├── windows
│   └── WindowsHelloEngine.dll
├── linux
│   └── LinuxPolkitEngine.so
└── macos
    └── LocalAuthenticationEngine.dylib
```