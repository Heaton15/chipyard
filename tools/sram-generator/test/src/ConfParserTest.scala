package sram.generator

import utest._

object ConfParserTest extends TestSuite {
  val conf = "name cc_dir_ext depth 1024 width 136 ports mrw mask_gran 17"

  val tests = Tests {
    test("conf_parsing") {
      val parsedConf                  = ConfParser.fromString(conf)
      val memPorts: Map[MemPort, Int] = Map(
        MaskedReadWritePort -> 1
      )
      val goldenConf = MemConf("cc_dir_ext", BigInt(1024), 136, memPorts, Some(17))
      assert(parsedConf == goldenConf)
    }

  }
}
