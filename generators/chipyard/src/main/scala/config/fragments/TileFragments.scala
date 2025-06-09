package chipyard.config

import chisel3._

import org.chipsalliance.cde.config.{Field, Parameters, Config}
import freechips.rocketchip.tile._
import freechips.rocketchip.subsystem._
import freechips.rocketchip.rocket.{RocketCoreParams, MulDivParams, DCacheParams, ICacheParams}

import testchipip.cosim.{TracePortKey, TracePortParams}

class WithL2TLBs(entries: Int) extends Config((site, here, up) => {
  case TilesLocated(InSubsystem) => up(TilesLocated(InSubsystem), site) map {
    case tp: RocketTileAttachParams => tp.copy(tileParams = tp.tileParams.copy(
      core = tp.tileParams.core.copy(nL2TLBEntries = entries)))
    case tp: boom.v3.common.BoomTileAttachParams => tp.copy(tileParams = tp.tileParams.copy(
      core = tp.tileParams.core.copy(nL2TLBEntries = entries)))
    case tp: boom.v4.common.BoomTileAttachParams => tp.copy(tileParams = tp.tileParams.copy(
      core = tp.tileParams.core.copy(nL2TLBEntries = entries)))
    case other => other
  }
})

class WithTraceIO extends Config((site, here, up) => {
  case TilesLocated(InSubsystem) => up(TilesLocated(InSubsystem), site) map {
    case tp: boom.v3.common.BoomTileAttachParams => tp.copy(tileParams = tp.tileParams.copy(
      core = tp.tileParams.core.copy(trace = true)))
    case tp: boom.v4.common.BoomTileAttachParams => tp.copy(tileParams = tp.tileParams.copy(
      core = tp.tileParams.core.copy(trace = true)))
    case other => other
  }
  case TracePortKey => Some(TracePortParams())
})

class WithNoTraceIO extends Config((site, here, up) => {
  case TilesLocated(InSubsystem) => up(TilesLocated(InSubsystem), site) map {
    case tp: boom.v3.common.BoomTileAttachParams => tp.copy(tileParams = tp.tileParams.copy(
      core = tp.tileParams.core.copy(trace = false)))
    case tp: boom.v4.common.BoomTileAttachParams => tp.copy(tileParams = tp.tileParams.copy(
      core = tp.tileParams.core.copy(trace = false)))
    case other => other
  }
  case TracePortKey => None
})

class WithNPerfCounters(n: Int = 29) extends Config((site, here, up) => {
  case TilesLocated(InSubsystem) => up(TilesLocated(InSubsystem), site) map {
    case tp: RocketTileAttachParams => tp.copy(tileParams = tp.tileParams.copy(
      core = tp.tileParams.core.copy(nPerfCounters = n)))
    case tp: boom.v3.common.BoomTileAttachParams => tp.copy(tileParams = tp.tileParams.copy(
      core = tp.tileParams.core.copy(nPerfCounters = n)))
    case tp: boom.v4.common.BoomTileAttachParams => tp.copy(tileParams = tp.tileParams.copy(
      core = tp.tileParams.core.copy(nPerfCounters = n)))
    case other => other
  }
})

// Add a monitor to RTL print the sinked packets into a file for debugging
class WithTraceArbiterMonitor extends Config((site, here, up) => {
  case TilesLocated(InSubsystem) => up(TilesLocated(InSubsystem), site) map {
    case tp: RocketTileAttachParams => tp.copy(tileParams = tp.tileParams.copy(
      traceParams = Some(tp.tileParams.traceParams.get.copy(useArbiterMonitor = true))))
  }
})

class WithNPMPs(n: Int = 8) extends Config((site, here, up) => {
  case TilesLocated(InSubsystem) => up(TilesLocated(InSubsystem), site) map {
    case tp: RocketTileAttachParams => tp.copy(tileParams = tp.tileParams.copy(
      core = tp.tileParams.core.copy(nPMPs = n)))
    case tp: boom.v3.common.BoomTileAttachParams => tp.copy(tileParams = tp.tileParams.copy(
      core = tp.tileParams.core.copy(nPMPs = n)))
    case tp: boom.v4.common.BoomTileAttachParams => tp.copy(tileParams = tp.tileParams.copy(
      core = tp.tileParams.core.copy(nPMPs = n)))
    case tp: chipyard.SpikeTileAttachParams => tp.copy(tileParams = tp.tileParams.copy(
      core = tp.tileParams.core.copy(nPMPs = n)))
    case other => other
  }
})


class WithRocketICacheScratchpad extends Config((site, here, up) => {
  case TilesLocated(InSubsystem) => up(TilesLocated(InSubsystem), site) map {
    case tp: RocketTileAttachParams => tp.copy(tileParams = tp.tileParams.copy(
      icache = tp.tileParams.icache.map(_.copy(itimAddr = Some(0x300000 + tp.tileParams.tileId * 0x10000)))
    ))
  }
})

class WithRocketDCacheScratchpad extends Config((site, here, up) => {
  case TilesLocated(InSubsystem) => up(TilesLocated(InSubsystem), site) map {
    case tp: RocketTileAttachParams => tp.copy(tileParams = tp.tileParams.copy(
      dcache = tp.tileParams.dcache.map(_.copy(nSets = 32, nWays = 1, scratch = Some(0x200000 + tp.tileParams.tileId * 0x10000)))
    ))
  }
})

// Use SV48
class WithSV48 extends Config((site, here, up) => {
  case TilesLocated(loc) => up(TilesLocated(loc), site) map {
    case tp: RocketTileAttachParams => tp.copy(tileParams = tp.tileParams.copy(core =
      tp.tileParams.core.copy(pgLevels = 4)))
    case tp: boom.v3.common.BoomTileAttachParams => tp.copy(tileParams = tp.tileParams.copy(core =
      tp.tileParams.core.copy(pgLevels = 4)))
    case tp: boom.v4.common.BoomTileAttachParams => tp.copy(tileParams = tp.tileParams.copy(core =
      tp.tileParams.core.copy(pgLevels = 4)))
  }
})
