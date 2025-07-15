/**
 * @file framework.h
 * @brief Central header for the project, used as a precompiled header.
 *
 * This header includes standard Windows and system headers needed by most
 * source files in the project. It helps speed up compilation by being precompiled.
 */

#pragma once

// Exclude rarely-used stuff from Windows headers
#define WIN32_LEAN_AND_MEAN

#include <windows.h>
