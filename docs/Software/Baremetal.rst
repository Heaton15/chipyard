.. _baremetal-programs:

Baremetal RISC-V Programs
==========================

To build baremetal RISC-V programs to run in simulation, we use the riscv64-unknown-elf cross-compiler and a fork of the libgloss board support package. To build such a program yourself, simply invoke the cross-compiler with the flags "-fno-common -fno-builtin-printf -specs=htif_nano.specs" and the link with the arguments "-static -specs=htif_nano.specs". For instance, if we want to run a "Hello, World" program in baremetal, we could do the following.

.. code:: c

    #include <stdio.h>

    int main(void)
    {
        printf("Hello, World!\n");
        return 0;
    }

.. code:: bash

    $ riscv64-unknown-elf-gcc -fno-common -fno-builtin-printf -specs=htif_nano.specs -c hello.c
    $ riscv64-unknown-elf-gcc -static -specs=htif_nano.specs hello.o -o hello.riscv
    $ spike hello.riscv
    Hello, World!

We have provided a set of example programs in the `tests/ directory <https://github.com/ucb-bar/chipyard/tree/master/tests>`_ in the chipyard repository.

The tests directory contains a CMakeLists.txt file that can be used to build the programs. To build the programs, you can use the following commands:

.. code:: bash

    $ cmake . -S ./ -B ./build/ -D CMAKE_BUILD_TYPE=Debug
    $ cmake --build ./build/ --target all
    $ spike hello.riscv
    Hello, World!


For more information about the libgloss port, take a look at `its README <https://github.com/ucb-bar/libgloss-htif/blob/master/README.md>`_.
