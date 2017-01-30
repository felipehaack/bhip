module App {

    export function toData<T>(response: angular.IHttpPromiseCallbackArg<T>): T {

        return response.data
    }
}