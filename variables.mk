#########################################################################################
# makefile variables shared across multiple makefiles
# - to use the help text, your Makefile should have a 'help' target that just
#   prints all the HELP_LINES
#########################################################################################
HELP_COMPILATION_VARIABLES = \
"   MILL_ARGS            = set additional mill command line options" \
"   MILL                 = override to change mill binary"

HELP_PROJECT_VARIABLES = \
"   SUB_PROJECT            = use the specific subproject default variables [$(SUB_PROJECT)]" \
"   BASE_PROJECT           = the Scala project that you should find the classes/packages in [$(BASE_PROJECT)]" \
"   MODEL                  = the top level module of the project in Chisel (normally the harness) [$(MODEL)]" \
"   VLOG_MODEL             = the top level module of the project in Firrtl/Verilog (normally the harness) [$(VLOG_MODEL)]" \
"   MODEL_PACKAGE          = the scala package to find the MODEL in [$(MODEL_PACKAGE)]" \
"   CONFIG                 = the configuration class to give the parameters for the project [$(CONFIG)]" \
"   CONFIG_PACKAGE         = the scala package to find the CONFIG class [$(CONFIG_PACKAGE)]" \
"   GENERATOR_PACKAGE      = the scala package to find the Generator class in [$(GENERATOR_PACKAGE)]" \
"   TB                     = testbench wrapper over the TestHarness needed to simulate in a verilog simulator [$(TB)]" \
"   TOP                    = top level module of the project (normally the module instantiated by the harness) [$(TOP)]"

HELP_SIMULATION_VARIABLES = \
"   BINARY                 = riscv elf binary that the simulator will run when using the run-binary* targets" \
"   BINARIES               = list of riscv elf binary that the simulator will run when using the run-binaries* targets" \
"   BINARIES_DIR           = directory of riscv elf binaries that the simulator will run when using the run-binaries* targets" \
"   BINARY_ARGS            = arguments to pass to each binary in run-binary targets (primarily meant for pk arguments)" \
"   LOADMEM                = riscv elf binary that should be loaded directly into simulated DRAM. LOADMEM=1 will load the BINARY elf" \
"   LOADARCH               = path to a architectural checkpoint directory that should end in .loadarch/, for restoring from a checkpoint" \
"   VERBOSE_FLAGS          = flags used when doing verbose simulation [$(VERBOSE_FLAGS)]" \
"   TIMEOUT_CYCLES         = number of clock cycles before simulator times out, defaults to 10000000" \
"   DUMP_BINARY            = set to '1' to disassemble the target binary"

# include default simulation rules
HELP_COMMANDS = \
"   help                   = display this help" \
"   default                = compiles non-debug simulator [./$(shell basename $(sim))]" \
"   debug                  = compiles debug simulator [./$(shell basename $(sim_debug))]" \
"   clean                  = remove all debug/non-debug simulators and intermediate files" \
"   clean-sim              = removes non-debug simulator and simulator-generated files" \
"   clean-sim-debug        = removes debug simulator and simulator-generated files"

HELP_LINES = "" \
	" design specifier variables:" \
	" ---------------------------" \
	$(HELP_PROJECT_VARIABLES) \
	"" \
	" compilation variables:" \
	" ----------------------" \
	$(HELP_COMPILATION_VARIABLES) \
	"" \
	" simulation variables:" \
	" ---------------------" \
	$(HELP_SIMULATION_VARIABLES) \
	"" \
	" some useful general commands:" \
	" -----------------------------" \
	$(HELP_COMMANDS) \
	""

#########################################################################################
# subproject overrides
# description:
#   - make it so that you only change 1 param to change most or all of them!
#   - mainly intended for quick developer setup for common flags
#########################################################################################
SUB_PROJECT ?= chipyard

ifeq ($(SUB_PROJECT),chipyard)
	BASE_PROJECT      ?= chipyard
	MODEL             ?= TestHarness
	VLOG_MODEL        ?= $(MODEL)
	MODEL_PACKAGE     ?= chipyard.harness
	CONFIG            ?= RocketConfig
	CONFIG_PACKAGE    ?= $(BASE_PROJECT)
	GENERATOR_PACKAGE ?= $(BASE_PROJECT)
	TB                ?= TestDriver
	TOP               ?= ChipTop
endif
# For TestChipIP developers running unit-tests
ifeq ($(SUB_PROJECT),testchipip)
	BASE_PROJECT      ?= chipyard
	MODEL             ?= TestHarness
	VLOG_MODEL        ?= $(MODEL)
	MODEL_PACKAGE     ?= chipyard.unittest
	CONFIG            ?= TestChipUnitTestConfig
	CONFIG_PACKAGE    ?= testchipip.test
	GENERATOR_PACKAGE ?= chipyard
	TB                ?= TestDriver
	TOP               ?= UnitTestSuite
endif
# For rocketchip developers running unit-tests
ifeq ($(SUB_PROJECT),rocketchip)
	BASE_PROJECT      ?= chipyard
	MODEL             ?= TestHarness
	VLOG_MODEL        ?= $(MODEL)
	MODEL_PACKAGE     ?= chipyard.unittest
	CONFIG            ?= TLSimpleUnitTestConfig
	CONFIG_PACKAGE    ?= freechips.rocketchip.unittest
	GENERATOR_PACKAGE ?= chipyard
	TB                ?= TestDriver
	TOP               ?= UnitTestSuite
endif
# For IceNet developers
ifeq ($(SUB_PROJECT),icenet)
	BASE_PROJECT      ?= chipyard
	MODEL             ?= TestHarness
	VLOG_MODEL        ?= $(MODEL)
	MODEL_PACKAGE     ?= chipyard.unittest
	CONFIG            ?= IceNetUnitTestConfig
	CONFIG_PACKAGE    ?= icenet
	GENERATOR_PACKAGE ?= chipyard
	TB                ?= TestDriver
	TOP               ?= UnitTestSuite
endif
# For Constellation developers
ifeq ($(SUB_PROJECT),constellation)
	BASE_PROJECT      ?= chipyard
	MODEL             ?= TestHarness
	VLOG_MODEL        ?= $(MODEL)
	MODEL_PACKAGE     ?= constellation.test
	CONFIG            ?= TestConfig00
	CONFIG_PACKAGE    ?= constellation.test
	GENERATOR_PACKAGE ?= chipyard
	TB                ?= TestDriver
	TOP               ?= NoC
endif

#########################################################################################
# path to rocket-chip and testchipip
#########################################################################################
ROCKETCHIP_DIR       = $(base_dir)/generators/rocket-chip
ROCKETCHIP_RSRCS_DIR = $(ROCKETCHIP_DIR)/src/main/resources
TESTCHIP_DIR         = $(base_dir)/generators/testchipip
TESTCHIP_RSRCS_DIR   = $(TESTCHIP_DIR)/src/main/resources
CHIPYARD_FIRRTL_DIR  = $(base_dir)/tools/firrtl
CHIPYARD_RSRCS_DIR   = $(base_dir)/generators/chipyard/src/main/resources

#########################################################################################
# names of various files needed to compile and run things
#########################################################################################
long_name = $(MODEL_PACKAGE).$(MODEL).$(CONFIG)

# chisel generated outputs
FIRRTL_FILE ?= $(build_dir)/$(long_name).fir
ANNO_FILE   ?= $(build_dir)/$(long_name).anno.json

# firtool compiler outputs
MFC_TOP_HRCHY_JSON ?= $(build_dir)/top_module_hierarchy.json
MFC_MODEL_HRCHY_JSON ?= $(build_dir)/model_module_hierarchy.json
MFC_SMEMS_CONF ?= $(build_dir)/$(long_name).mems.conf
# hardcoded firtool outputs
MFC_FILELIST = $(GEN_COLLATERAL_DIR)/filelist.f
MFC_BB_MODS_FILELIST = $(GEN_COLLATERAL_DIR)/firrtl_black_box_resource_files.f
MFC_TOP_SMEMS_JSON = $(GEN_COLLATERAL_DIR)/metadata/seq_mems.json

# macrocompiler smems in/output
TOP_SMEMS_MFC_FILELIST   = $(TOP_MEMS_GEN_COLLATERAL_DIR)/filelist.f
MODEL_SMEMS_MFC_FILELIST = $(MODEL_MEMS_GEN_COLLATERAL_DIR)/filelist.f

TOP_SMEMS_PREFIX ?= top.mems
MODEL_SMEMS_PREFIX ?= model.mems

TOP_SMEMS_CONF       = $(build_dir)/$(long_name).$(TOP_SMEMS_PREFIX).conf
TOP_SMEMS_FIR        = $(build_dir)/$(long_name).$(TOP_SMEMS_PREFIX).fir
TOP_SMEMS_FILELIST   = $(build_dir)/$(long_name).$(TOP_SMEMS_PREFIX).f
MODEL_SMEMS_CONF     = $(build_dir)/$(long_name).$(MODEL_SMEMS_PREFIX).conf
MODEL_SMEMS_FIR      = $(build_dir)/$(long_name).$(MODEL_SMEMS_PREFIX).fir
MODEL_SMEMS_FILELIST = $(build_dir)/$(long_name).$(MODEL_SMEMS_PREFIX).f

# top module files to include
TOP_MODS_FILELIST ?= $(build_dir)/$(long_name).top.f
# model module files to include (not including top modules)
MODEL_MODS_FILELIST ?= $(build_dir)/$(long_name).model.f
# list of all blackbox files (may be included in the top/model.f files)
# this has the build_dir appended
BB_MODS_FILELIST ?= $(build_dir)/$(long_name).bb.f
# all module files to include (top, model, bb included)
ALL_MODS_FILELIST ?= $(build_dir)/$(long_name).all.f
# all Chisel layers files to include
ALL_LAYERS_FILELIST ?= $(build_dir)/$(long_name).all_layers.f
# top module files for synthesis flow, paths are relative to the build directory
SYN_MODS_FILELIST ?= $(build_dir)/$(long_name).syn.f

# external filelists. Users, or project-supplied make fragments can append filelists
# with absolute paths here
EXT_FILELISTS ?=
# external verilog incdirs. Users, or project-supplied make fragments can append to this
EXT_INCDIRS ?=

# files that contain lists of files needed for VCS or Verilator simulation
SIM_FILE_REQS =
sim_files              ?= $(build_dir)/sim_files.f
# single file that contains all files needed for VCS or Verilator simulation (unique and without .h's)
sim_common_files       ?= $(build_dir)/sim_files.common.f

# The build flow generates a number of filelists with absolute paths (from `$(base_dir)`), but we
# want relative filelists that work across machines. The build flow will generate the `.f.abs`
# files with absolute paths, and we'll convert those into `.f` files without the absolute paths.
# Note that `$(SYN_MODS_FILELIST)` isn't in this list, since it isn't generated from an
# absolute-path filelist of the same name.
RELATIVE_FILELISTS = \
	$(TOP_MODS_FILELIST) \
	$(MODEL_MODS_FILELIST) \
	$(BB_MODS_FILELIST) \
	$(ALL_MODS_FILELIST) \
	$(ALL_LAYERS_FILELIST) \
	$(TOP_SMEMS_FILELIST) \
	$(MODEL_SMEMS_FILELIST) \
	$(sim_files) \
	$(sim_common_files)
ABSOLUTE_FILELISTS = $(addsuffix .abs,$(RELATIVE_FILELISTS))

#########################################################################################
# build tool arguments
#########################################################################################
# By default, use colorful output from Chisel
export CHISEL_USE_COLOR ?= true

# Mill's client-server mode relies on locking files (using Java's
# `FileLock.tryLock` method). This is unreliable on network file systems, so we
# disable the mill server with the `--no-server` flag.
MILL_ARGS ?= --no-server
MILL ?= ./mill

#########################################################################################
# output directory for tests
#########################################################################################
output_dir=$(sim_dir)/output/$(long_name)

#########################################################################################
# helper variables to run binaries
#########################################################################################
PERMISSIVE_ON=+permissive
PERMISSIVE_OFF=+permissive-off
BINARY ?=
BINARIES ?=
BINARY_ARGS ?=
override SIM_FLAGS += +dramsim +dramsim_ini_dir=$(TESTCHIP_DIR)/src/main/resources/dramsim2_ini +max-cycles=$(TIMEOUT_CYCLES)
VERBOSE_FLAGS ?= +verbose
# get_out_name is a function, 1st argument is the binary
get_out_name = $(subst $() $(),_,$(notdir $(basename $(1))))
LOADMEM ?=
LOADARCH ?=
DUMP_BINARY ?= 1

ifneq ($(LOADARCH),)
override BINARY = $(addsuffix /mem.elf,$(LOADARCH))
override BINARIES = $(addsuffix /mem.elf,$(LOADARCH))
override get_out_name = $(shell basename $(dir $(1)))
override LOADMEM = 1
endif

ifneq ($(BINARIES_DIR),)
override BINARIES = $(shell find -L $(BINARIES_DIR) -type f -print 2> /dev/null)
endif

#########################################################################################
# build output directory for compilation
#########################################################################################
# output for all project builds
generated_src_name ?=generated-src
gen_dir             =$(sim_dir)/$(generated_src_name)
# per-project output directory
build_dir           =$(gen_dir)/$(long_name)
# final generated collateral per-project
GEN_COLLATERAL_DIR            ?= $(build_dir)/gen-collateral
TOP_MEMS_GEN_COLLATERAL_DIR    = $(GEN_COLLATERAL_DIR)/top-mems
MODEL_MEMS_GEN_COLLATERAL_DIR  = $(GEN_COLLATERAL_DIR)/model-mems

#########################################################################################
# simulation variables
#########################################################################################
TIMEOUT_CYCLES = 10000000

# legacy timeout_cycles handling
timeout_cycles ?=
ifneq ($(timeout_cycles),)
TIMEOUT_CYCLES=$(timeout_cycles)
endif
