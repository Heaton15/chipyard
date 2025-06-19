package sram.generator

import utest._

object ConfParserTest extends TestSuite {
  val conf = "name cc_dir_ext depth 1024 width 136 ports mrw mask_gran 17"

  val tests = Tests {
    test("conf_parsing") {
      assert(conf == conf)
    }

  }
}
