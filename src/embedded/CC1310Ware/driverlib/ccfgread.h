/******************************************************************************
*  Filename:       ccfgread.h
*  Revised:        2015-08-04 11:44:20 +0200 (Tue, 04 Aug 2015)
*  Revision:       44329
*
*  Description:    API for reading CCFG.
*
*  Copyright (c) 2015, Texas Instruments Incorporated
*  All rights reserved.
*
*  Redistribution and use in source and binary forms, with or without
*  modification, are permitted provided that the following conditions are met:
*
*  1) Redistributions of source code must retain the above copyright notice,
*     this list of conditions and the following disclaimer.
*
*  2) Redistributions in binary form must reproduce the above copyright notice,
*     this list of conditions and the following disclaimer in the documentation
*     and/or other materials provided with the distribution.
*
*  3) Neither the name of the ORGANIZATION nor the names of its contributors may
*     be used to endorse or promote products derived from this software without
*     specific prior written permission.
*
*  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
*  AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
*  IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
*  ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
*  LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
*  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
*  SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
*  INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
*  CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
*  ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
*  POSSIBILITY OF SUCH DAMAGE.
*
******************************************************************************/

//*****************************************************************************
//
//! \addtogroup system_control_group
//! @{
//! \addtogroup ccfgread_api
//! @{
//
//*****************************************************************************

#ifndef __CCFGREAD_H__
#define __CCFGREAD_H__

//*****************************************************************************
//
// If building with a C++ compiler, make all of the definitions in this header
// have a C binding.
//
//*****************************************************************************
#ifdef __cplusplus
extern "C"
{
#endif

#include <stdbool.h>
#include <stdint.h>
#include "../../CC1310Ware/inc/hw_types.h"
#include "../../CC1310Ware/inc/hw_memmap.h"
#include "../../CC1310Ware/inc/hw_ccfg.h"

//*****************************************************************************
//
// General constants and defines
//
//*****************************************************************************


//*****************************************************************************
//
// API Functions and prototypes
//
//*****************************************************************************

//*****************************************************************************
//
//! \brief Read EXT_LF_CLK_DIO from CCFG.
//!
//! \return Value of CCFG field CCFG_EXT_LF_CLK_DIO
//
//*****************************************************************************
__STATIC_INLINE bool
CCFGRead_EXT_LF_CLK_DIO( void )
{
    return (( HWREG( CCFG_BASE + CCFG_O_EXT_LF_CLK ) &
        CCFG_EXT_LF_CLK_DIO_M ) >>
        CCFG_EXT_LF_CLK_DIO_S ) ;
}

//*****************************************************************************
//
//! \brief Read DIS_GPRAM from CCFG.
//!
//! \return Value of CCFG field CCFG_SIZE_AND_DIS_FLAGS_DIS_GPRAM
//
//*****************************************************************************
__STATIC_INLINE bool
CCFGRead_DIS_GPRAM( void )
{
    return (( HWREG( CCFG_BASE + CCFG_O_SIZE_AND_DIS_FLAGS ) &
        CCFG_SIZE_AND_DIS_FLAGS_DIS_GPRAM_M ) >>
        CCFG_SIZE_AND_DIS_FLAGS_DIS_GPRAM_S ) ;
}

//*****************************************************************************
//
// Defines the possible values returned from CCFGRead_SCLK_LF_OPTION()
//
//*****************************************************************************
#define SCLK_LF_OPTION_XOSC_HF_DLF     0
#define SCLK_LF_OPTION_EXTERNAL        1
#define SCLK_LF_OPTION_XOSC_LF         2
#define SCLK_LF_OPTION_RCOSC_LF        3

//*****************************************************************************
//
//! \brief Read SCLK_LF_OPTION from CCFG.
//!
//! \return Value of CCFG field CCFG_MODE_CONF_SCLK_LF_OPTION.
//! Returns one of the following:
//! - \ref SCLK_LF_OPTION_XOSC_HF_DLF
//! - \ref SCLK_LF_OPTION_EXTERNAL
//! - \ref SCLK_LF_OPTION_XOSC_LF
//! - \ref SCLK_LF_OPTION_RCOSC_LF
//
//*****************************************************************************
__STATIC_INLINE uint32_t
CCFGRead_SCLK_LF_OPTION( void )
{
    return (( HWREG( CCFG_BASE + CCFG_O_MODE_CONF ) &
        CCFG_MODE_CONF_SCLK_LF_OPTION_M ) >>
        CCFG_MODE_CONF_SCLK_LF_OPTION_S ) ;
}

//*****************************************************************************
//
// Mark the end of the C bindings section for C++ compilers.
//
//*****************************************************************************
#ifdef __cplusplus
}
#endif

#endif // __AUX_SMPH_H__

//*****************************************************************************
//
//! Close the Doxygen group.
//! @}
//! @}
//
//*****************************************************************************
