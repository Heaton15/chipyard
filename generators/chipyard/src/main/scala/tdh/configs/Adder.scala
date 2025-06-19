package chipyard.tdh

import chisel3._
import chipyard.tdh.TopModuleKey
import org.chipsalliance.cde.config.Config


// This is just a test for the custom BlockHarness that is build
class Adder(dataWidth: Int) extends Module {
  val a = IO(Input(UInt(dataWidth.W)))
  val b = IO(Input(UInt(dataWidth.W)))
  val z = IO(Output(UInt((dataWidth + 1).W)))
  z :<= a +& b
}

class AdderConfig extends Config((_, _, _) => { case TopModuleKey =>
      Some(() => new Adder(8))
    })
