package sram.generator

/** ConfParser object which is responsible for parsing the memory configuration
  *  outputs from the Chisel compiler.
  */
object ConfParser {
  val regex = raw"\s*name\s+(\w+)\s+depth\s+(\d+)\s+width\s+(\d+)\s+ports\s+([^\s]+)\s+(?:mask_gran\s+(\d+))?\s*".r

  def fromString(s: String): MemConf = {
    val result = s match {
      case this.regex(name, depth, width, ports, mask_gran) => {
        val memPorts  = ports.split(",").map(MemPort.fromString).toSeq
        val memGroups = MemPort.groupMemPorts(memPorts)
        Some(MemConf(name, BigInt(depth), width.toInt, memGroups, Option(mask_gran).map(_.toInt)))
      }
      case _ => None
    }
    result.getOrElse(throw new Exception("Memory configuration is not valid"))
  }

}
