// src/api/axiosInstance.ts

import axios from 'axios';

type RequestOptions = axios.AxiosRequestConfig;

// Интерфейс обертки для ответа от API
export interface Response<T> {
  data: T;               // Данные, возвращаемые от API
  status: number;        // HTTP статус
  statusText: string;    // Статус текста
}

class ApiClient {
  private axiosInstance;

  public constructor() {
    this.axiosInstance = axios.create();
  }

  public async get<ResponseData>(
    endpoint: string,
    options: RequestOptions = {}
  ): Promise<Response<ResponseData>> {
    const { headers, params } = options;
    try {
      const axiosResponse = await this.axiosInstance.get(endpoint, {
        headers,
        params,
      });
      return this.wrapResponse<ResponseData>(axiosResponse);
    } catch (error) {
      throw error;
    }
  }

  public async put<ResponseData, RequestData>(
    endpoint: string,
    body: RequestData,
    options: RequestOptions = {}
  ): Promise<Response<ResponseData>> {
    const { headers, params } = options;
    try {
      const axiosResponse = await this.axiosInstance.put(endpoint, body, {
        headers,
        params,
      });
      return this.wrapResponse<ResponseData>(axiosResponse);
    } catch (error) {
      throw error;
    }
  }

  public async post<ResponseData, RequestData>(
    endpoint: string,
    body: RequestData,
    options: RequestOptions = {}
  ): Promise<Response<ResponseData>> {
    const { headers, params } = options;
    try {
      const axiosResponse = await this.axiosInstance.post(endpoint, body, {
        headers,
        params,
      });
      return this.wrapResponse<ResponseData>(axiosResponse);
    } catch (error) {
      throw error;
    }
  }
  public async delete<ResponseData>(
    endpoint: string,
    options: RequestOptions = {}
  ): Promise<Response<ResponseData>> {
    const { headers, params } = options;
    try {
      const axiosResponse = await this.axiosInstance.delete(endpoint, {
        headers,
        params,
      });
      return this.wrapResponse<ResponseData>(axiosResponse);
    } catch (error) {
      throw error;
    }
  }

  public async patch<ResponseData, RequestData>(
    endpoint: string,
    body: RequestData,
    options: RequestOptions = {}
  ): Promise<Response<ResponseData>> {
    const { headers, params } = options;
    try {
      const axiosResponse = await this.axiosInstance.patch(endpoint, body, {
        headers,
        params,
      });
      return this.wrapResponse<ResponseData>(axiosResponse);
    } catch (error) {
      throw error;
    }
  }

  // Вспомогательный метод для обёртки ответа
  private wrapResponse<T>(axiosResponse: axios.AxiosResponse): Response<T> {
    return {
      data: axiosResponse.data,
      status: axiosResponse.status,
      statusText: axiosResponse.statusText,
    };
  }
}

// Экземпляр API клиента
const apiClient = new ApiClient();

// Экспорт методов API для удобства
export const post = apiClient.post.bind(apiClient);
export const put = apiClient.put.bind(apiClient);
export const get = apiClient.get.bind(apiClient);
export const del = apiClient.delete.bind(apiClient);
export const patch = apiClient.patch.bind(apiClient);
