package sram.generator

sealed abstract class MemPort(val name: String) { override def toString = name }
case object ReadPort            extends MemPort("read")
case object WritePort           extends MemPort("write")
case object MaskedWritePort     extends MemPort("mwrite")
case object ReadWritePort       extends MemPort("rw")
case object MaskedReadWritePort extends MemPort("mrw")

object MemPort {

  // This is the order that ports will render in MemConf.portsStr
  val ordered: Seq[MemPort] = Seq(
    MaskedReadWritePort,
    MaskedWritePort,
    ReadWritePort,
    WritePort,
    ReadPort,
  )

  /** Transforms the memory port string from the Chisel compiler into a memory
    * port object
    */
  def fromString(s: String): MemPort = s match {
    case ReadPort.name            => ReadPort
    case WritePort.name           => WritePort
    case MaskedWritePort.name     => MaskedWritePort
    case ReadWritePort.name       => ReadWritePort
    case MaskedReadWritePort.name => MaskedReadWritePort
    case _                        => throw new Exception(s"Invalid memory port $s")
  }

  /** Returns a map of containing the count of memory points for a given `mem`
    */
  def groupMemPorts(memList: Seq[MemPort]): Map[MemPort, Int] = memList.groupBy(identity).view.mapValues(_.size).toMap
}

case class MemConf(name: String, depth: BigInt, width: Int, ports: Map[MemPort, Int], maskGranularity: Option[Int]) {


}
