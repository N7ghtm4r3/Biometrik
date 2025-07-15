/**
 * @file pch.h
 * @brief Precompiled header including framework and WinRT namespaces for Windows Hello.
 *
 * This header includes the main Windows framework header and
 * WinRT namespaces required for modern Windows authentication APIs.
 *
 * It is intended to speed up compilation by pre-compiling these headers.
 * @author
 * Tecknobit - N7ghm4r3
 *
 * @version 1.0.0
 */

#ifndef PCH_H
#define PCH_H

#include "framework.h"
#include <winrt/Windows.Security.Credentials.UI.h>
#include <winrt/Windows.Foundation.h>

#endif