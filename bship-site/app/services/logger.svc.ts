module App {

    const enum LogType {
        Debug,
        Info,
        Warning,
        Error,
        Success
    }

    export interface ILog {
        debug(source: string, message?: string, data?: any): void;
        info(source: string, message?: string, data?: any): void;
        error(source: string, message?: string, data?: any): void;
        warn(source: string, message?: string, data?: any): void;
    }

    export interface ILoggerService {
        log(logType: LogType, message: string, data?: any);
    }

    export interface ILoggerFactory {
        (sourceId: string): ILog;
    }

    export class LoggerService implements ILoggerService {

        static id = "loggerService";

        /*@ngInject*/
        constructor(private $log: ng.ILogService) {

        }

        log(logType: LogType, message: string, data?: any) {
            switch (logType) {

                case LogType.Debug:
                    this.$log.debug(message, data);
                    break;
                case LogType.Info:
                    this.$log.info(message, data);
                    break;
                case LogType.Error:
                    this.$log.error(message, data);
                    break;
                case LogType.Warning:
                    this.$log.warn(message, data);
                    break;
                default:
                    this.$log.log(message, data);
                    break;
            }
        }
    }

    export class Logger implements ILog {

        constructor(private sourceId: string,
                    private loggerService: ILoggerService) {

        }

        debug(source: string, message?: string, data?: any) {
            this._log(this.sourceId, source, LogType.Debug, message, data);
        }

        info(source: string, message?: string, data?: any) {
            this._log(this.sourceId, source, LogType.Info, message, data);
        }

        error(source: string, message?: string, data?: any) {
            this._log(this.sourceId, source, LogType.Error, message, data);
        }

        warn(source: string, message?: string, data?: any) {
            this._log(this.sourceId, source, LogType.Warning, message, data);
        }

        private _log(sourceId: string, source: string, logType: LogType, message?: string, data?: any) {
            let msg = `[${sourceId}::${source}] ${message}`;
            this.loggerService.log(logType, msg, data);
        }

    }


    angular.module(App.Module).service(LoggerService.id, LoggerService)
        .factory("loggerFactory", (loggerService) => {
            return (sourceId): ILog => {
                return new Logger(sourceId, loggerService);
            };
        });
}