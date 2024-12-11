import { get, put, post, del, patch } from '@/shared/repositories/ApiClient'
import axios from 'axios';

export type DefaultResponse = {
  message: string,
  error?: string
}
type TableColumns = {
  name: string,
  typeName: string,
}
export type Table = {
  name: string | null,
  columns: Array<TableColumns>,
  data: Array<Array<string>>
}
export type SetTableValueRequest = {
  row: number,
  column: number,
  value: string,
}
export type CreateTableResponse = DefaultResponse;
export type SetTableValueResponse = DefaultResponse;
export type UpdateTableResponse = DefaultResponse;
export type DeleteTableResponse = DefaultResponse;
export type TableList = Array<string>;
export type ColumnAddRequest = {type: "Integer | Date | GeoPosition | Time | Number", name?: string};
const apiURL = import.meta.env.VITE_API_BASE_URL;
const defaultOptions : axios.AxiosRequestConfig = {
  headers: {
    'Accept': 'application/json',
    'Cross-origin': import.meta.env.VITE_API_BASE_URL,
  }
}


export const TableRepository = {
  createTable: (tableName:string) => {
    return post<CreateTableResponse,any>(apiURL+"/tables/"+tableName,{}, defaultOptions);
  },
  deleteTable: (tableName:string) => {
    return del<DeleteTableResponse>(apiURL+"/tables/"+tableName, defaultOptions);
  },
  updateTable: (tableName:string) => {
    return patch<UpdateTableResponse,null>(apiURL+"/tables/"+tableName, null, defaultOptions);
  },
  removeColumn: (tableName:string, colIndex: number) => {
    return del<DefaultResponse>(apiURL+"/tables/"+tableName+"/column/"+colIndex);
  },
  removeRow: (tableName:string, rowIndex: number) => {
    return del<DefaultResponse>(apiURL+"/tables/"+tableName+"/row/"+rowIndex);
  },
  getTable: (tableName:string) => {
    return get<Table>(apiURL+"/tables/"+tableName, defaultOptions);
  },
  addRow: (tableName:string) => {
    return post<DefaultResponse, null>(apiURL+"/tables/"+tableName+"/row", null, defaultOptions);
  },
  addColumn: (tableName:string, request: ColumnAddRequest) => {
    return post<DefaultResponse, ColumnAddRequest>(apiURL+"/tables/"+tableName+"/column", request, defaultOptions);
  },
  getTablesList: ()=>{
    return get<TableList>(apiURL+"/tables",defaultOptions);
  },
  setValue: (tableName: string, row: number, column: number, value: string) => {
    return put<SetTableValueResponse, SetTableValueRequest>(apiURL+"/tables/"+tableName,{
      row:row,
      column:column,
      value:value,
    }, defaultOptions);
  }
}
