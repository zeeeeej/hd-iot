package com.yunext.iot.di

import com.yunext.iot.datasource.UartDatasource
import com.yunext.iot.datasource.UartDatasourceImpl
import com.yunext.iot.model.UartManager
import com.yunext.iot.model.UartManagerImpl
import com.yunext.iot.repository.ProtocolRepository
import com.yunext.iot.repository.ProtocolRepositoryImpl
import com.yunext.iot.repository.UartRepository
import com.yunext.iot.repository.UartRepositoryImpl
import com.yunext.iot.ui.vm.HomeVM
import com.yunext.iot.ui.vm.SerialProtocolVM
import com.yunext.iot.ui.vm.Snapshot
import kotlinx.serialization.json.Json
import org.koin.core.Koin
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

val appModule = module {
    single<UartManager> { UartManagerImpl() }
//    single<UserDatasource> { UserDatasourceImpl(get()) } //bind UserDatasource::class
//    single<HttpDatasource> { HttpDatasourceImpl() } //bind UserDatasource::class
    single<UartRepository> { UartRepositoryImpl(get()) }
    single<UartDatasource> { UartDatasourceImpl(get()) }
    single<ProtocolRepository> { ProtocolRepositoryImpl() }
    single<Snapshot> { Snapshot(get()) }

    single<String> { "abc" }
    factory { params ->
        HomeVM(get())
    }
    factory { params ->
        SerialProtocolVM(get(),get())
    }

//    factory { params ->
//        HomeRootViewModel(get(), params.get())
//    }
//
//    factory { params ->
//        MainViewModel(get(), params.get())
//    }
//    factory { params ->
//        ScanViewModel(get(), params.get())
//    }
//    factory { params ->
//        ProductionViewModel(get(), params.get(), params.get())
//    }
}


//expect val platformViewModelModule: Module
//expect val platformUserStoreModule: Module

object KoinInit {
    fun init(appDeclaration: KoinAppDeclaration): Koin {
        println("KoinInit init")
//        Napier.base(DebugAntilog())
        return startKoin {
            appDeclaration()
            modules(
                /*platformUserStoreModule,*/ appModule, /*platformViewModelModule*/
            )
        }.koin
    }
}

val myJson = Json {
    ignoreUnknownKeys = true
    prettyPrint = true
}
