const option = {
    port: 848,
    baseAuth: 'root:pwd', //get基本认证
    dev: false, //是否打印详细日志
    mongodb: 'mongodb://localhost/api',
};
const mime = require('mime-types');
const http = require('http');
const url = require('url');
const fs = require('fs');
const logger = require('log4js').getLogger('edc-server');
const debug = logger.debug;
const info = logger.info;
const formidable = require('formidable');
const mongoose = require('mongoose');
mongoose.connect(option.mongodb, {useMongoClient: true});
mongoose.Promise = global.Promise;
const guid = function (time = 1) {
    let result = '';
    for (let i = 0; i < time; i++) {
        result += (function () {
            let c = new Date(),
                b = c.getSeconds() + '',
                d = c.getMinutes() + '',
                e = c.getMilliseconds() + '';
            for (let i = b.length, j = 2; i < j; i++) b = '0' + b;
            for (let i = d.length, j = 2; i < j; i++) d = '0' + d;
            for (let i = e.length, j = 3; i < j; i++) e = '0' + e;
            return b + d + e + (((1 + Math.random()) * 0x10000) | 0).toString(16).substring(1) + (((1 + Math.random()) * 0x10000) | 0).toString(16);
        })();
    }
    return result.toUpperCase();
};
const promise = function (fn, target) {
    return (...args) => {
        return new Promise((resolve, reject) => {
            args.push(function(err, ...data){
                if (err === null || err === undefined) return resolve.apply(this, data);
                return reject(err);
            });
            fn.apply(target, args);
        });
    };
};
const Api = mongoose.model('Api', {
    appname: String,
    description: Array,
    version: String,
    clz: String
});
const FindApi = promise(Api.find,Api);
const fs_readFile = promise(fs.readFile);
const ejs = require('ejs');
logger.debug = function () {
    if (option.dev === true) debug.call(logger, Array.prototype.slice.call(arguments, 0));
};
logger.info = function () {
    if (option.dev === true) info.call(logger, Array.prototype.slice.call(arguments, 0));
};
/**
 * 打印response数据
 * @param info
 * @param contentType
 * @param contentLength
 * @param response
 * @param status
 * @param isFile
 * @param result
 * @param location
 */
const responseWrite = function ({info = '', contentType = 'text/plain;charset=utf-8', contentLength = 0, response, status = 200, isFile = false, result = false, location = false} = {}) {
    let header = {
        'Access-Control-Allow-Headers': 'X-File-Name, X-File-Type, X-File-Size, authorization',
        'Access-Control-Allow-Methods': 'OPTIONS, HEAD, POST, GET',
        'Access-Control-Allow-Origin': '*',
        'Access-Control-Expose-Headers': 'X-Log, X-Reqid',
        'Access-Control-Max-Age': '2592000',
        'Cache-Control': 'no-store, no-cache, must-revalidate',
        'Connection': 'keep-alive',
        'Content-Type': contentType,
        'Content-Length': contentLength,
        'Date': new Date().toUTCString(),
        'Pragma': 'no-cache',
        'Server': 'nginx',
        'X-Content-Type-Options': 'nosniff',
        'X-Reqid': guid()
    };
    if (result !== false) {
        header.result = result;
    }
    if (location !== false) {
        header.Location = location;
    }
    if (info !== undefined) {
        if (isFile === false) {
            header['Content-Length'] = Buffer.byteLength(info.toString());
            response.writeHead(status, header);
            response.write(info);
        } else {
            response.writeHead(status, header);
            response.write(info, 'binary');
        }
    }
    return response.end();
};
const throwIf = (condition, message) => {
    if (condition) throw message;
};
const callback_method = {
    async 'post'(request, response) {
        let form = new formidable.IncomingForm();
        let parse = promise(form.parse, form);
        let data = await parse(request);
        try {
            throwIf(!data.appname, '必须输入appname');
            throwIf(!data.description, '必须输入description');
            throwIf(!data.version, '必须输入version');
            throwIf(!data.clz, '必须输入clz');
        }
        catch (e) {
            return responseWrite({
                response: response,
                info: 'bad request',
                status: 500
            });
        }
        let apis = await FindApi({
            appname : data.appname,
            version : data.version
        });
        apis.forEach(async iapi => (promise(iapi.remove, iapi))());
        let api = new Api(data);
        try {
            await (promise(api.save, api))();
        } catch (e) {
            throwIf(true, e);
        }
        responseWrite({
            response: response,
            info: 'success!'
        });
    },
    async 'get'(request, response) {
        let authorization = request.headers['authorization'];
        if (!authorization || new Buffer(authorization.substr(6), 'base64').toString() !== option.baseAuth) {
            response.writeHead(401, {
                'content-Type': 'text/plain',
                'WWW-Authenticate': "Basic realm='family'"
            });
            return response.end();
        }
        let fields = url.parse(request.url, true).query;
        let appname = fields.appname;
        let version = fields.version;


        let apis = await FindApi({appname,version});
        try {
            throwIf(!apis.length, 'api不存在');
        }
        catch (e) {
            return responseWrite({
                response: response,
                info: 'bad request',
                status: 500
            });
        }

        let html = await fs_readFile('api.html');
        html = ejs.render(html.toString(),apis[0],{
            cache : option.dev === false,
            filename : "api"
        });

        return responseWrite({
            response: response,
            contentType: mime.contentType('html'),
            info : html
        });
    }
};
/**
 * http method core
 * @param request
 * @param response
 * @returns {Promise.<void>}
 */
const callback = async function (request, response) {
    let method = request.method.toLowerCase();
    let req_url = decodeURIComponent(request.url);
    let urlParse = url.parse(req_url, true);
    let pathname_ = urlParse.pathname.replace(/%20|\.\.|\s/g, '');
    if (pathname_ === '/favicon.ico') return responseWrite({
        response: response,
        contentType: mime.contentType('ico')
    });
    if (callback_method[method]) {
        try {
            callback_method[method](request, response);
        }
        catch (e) {
            responseWrite({
                response: response,
                info: 'bad request',
                status: 500
            });
        }
    }
    else {
        responseWrite({
            response: response,
            info: 'error method',
            status: 405
        });
    }
};
let server = http.createServer(callback);
server.listen(option.port);
logger.info(`listening: worker ${process.pid}, Address: /: ${option.port}`);