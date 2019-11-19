import { Context, Logger } from "@azure/functions"

 const fixtureLogger: Logger = Object.assign(
    (...args: any[]) => {},
    {
        info(...args: any[]){},
        error  (...args: any[]) {},
        warn  (...args: any[]){},
        verbose  (...args: any[]) {}
    }
  )


const contextFixture: Context = {
    invocationId:  "",
    executionContext: null,
    bindings: {},
    bindingData: {},
    bindingDefinitions: [],
    log: fixtureLogger,
    req: null,
    res: null,
    done: function(err?: Error | string | null, result?: any) {}
}

export default contextFixture;