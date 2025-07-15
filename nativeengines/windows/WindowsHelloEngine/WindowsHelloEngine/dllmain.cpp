/**
 * @file dllmain.cpp
 * @brief Entry point for the Windows DLL module.
 *
 * This file contains the `DllMain` function which is called by the system
 * when processes and threads are initialized and terminated, or upon calls to LoadLibrary and FreeLibrary.
 *
 * The function currently performs no specific action on these events.
 *
 * @author
 * Tecknobit - N7ghm4r3
 *
 * @version 1.0.0
 */

#include "pch.h"

/**
 * @brief DLL entry point called by the system on process and thread events.
 *
 * @param hModule Handle to the DLL module
 * @param ul_reason_for_call Reason code why the function is called
 * @param lpReserved Reserved, usage depends on `ul_reason_for_call`
 *
 * @return TRUE to indicate successful initialization or cleanup
 */
BOOL APIENTRY DllMain( HMODULE hModule,
                       DWORD  ul_reason_for_call,
                       LPVOID lpReserved
                     )
{
    switch (ul_reason_for_call)
    {
    case DLL_PROCESS_ATTACH:
    case DLL_THREAD_ATTACH:
    case DLL_THREAD_DETACH:
    case DLL_PROCESS_DETACH:
        break;
    }
    return TRUE;
}

