package chipyard.tdh

import org.chipsalliance.cde.config.Field

case object TopModuleKey extends Field[Option[() => BaseModule]](None)
case object LazyTopModuleKey extends Field[Option[() => BaseModule]](None)

class BlockHarness(implicit val p: Parameters) extends Module {

}
