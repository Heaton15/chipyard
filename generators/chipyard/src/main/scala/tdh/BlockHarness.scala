package chipyard.tdh

import chisel3._
import chisel3.experimental.BaseModule
import chisel3.reflect.DataMirror

import org.chipsalliance.cde.config.{Field, Parameters}

case object TopModuleKey extends Field[Option[() => BaseModule]](None)

class BlockHarness(implicit val p: Parameters) extends Module {

  // Instantiate the custom block we are designing
  // If more types are added, we can pattern match on the keys instead
  val dut = Module(
    p(TopModuleKey).getOrElse(throw new Exception(s"Unable to instantiate module referenced by TopModuleKey"))()
  )

  // This has to go to the TestDriver if that still exists?
  val success = IO(Output(Bool()))
  success :<= false.B

  val io = DataMirror
    .modulePorts(dut)
    .filter { case (name, _) => name != "clock" && name != "reset" }
    .foreach { case (name, port) =>
      val io = IO(chiselTypeOf(port))
      io.suggestName(name)
      DataMirror.directionOf(io) match {
        case ActualDirection.Input                 => port :<= io
        case ActualDirection.Output                => io   :<= port
        case ActualDirection.Bidirectional.Default => io   :<= port
        case ActualDirection.Bidirectional.Flipped => port :<= io
        case ActualDirection.Empty                 => println("Skipping ActualDirection.Empty in BlockHarness")
        case ActualDirection.Unspecified           => println("Skipping ActualDirection.Unspecified in BlockHarness")
        case _ => throw new Exception("Unrecognized port direction encountered for Datamirror.directionOf(...)")
      }

    }

}
